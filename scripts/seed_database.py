#!/usr/bin/env python3
"""
Mahallu Manager - Database Seeder
=================================
Seeds the default admin user (and a few other users) into the Mahallu Manager
SQLite database. Run this AFTER installing the APK and enabling USB debugging.

Usage:
    python3 seed_mahallu.py            # seeds via adb shell run-as
    python3 seed_mahallu.py --print    # just print the SQL

This is a workaround for the seed-data bug in the debug APK. Once the user
exists, normal login (admin / admin123) works.
"""
import hashlib
import hmac
import base64
import os
import struct
import subprocess
import sys

PBKDF2_ITER = 120000
SALT_LEN = 16
KEY_LEN = 32  # 256 bits

def pbkdf2_hash(password: str, salt: bytes) -> bytes:
    """PBKDF2-HMAC-SHA256 in pure Python (no hashlib.pbkdf2 for compat)."""
    return hashlib.pbkdf2_hmac('sha256', password.encode('utf-8'), salt, PBKDF2_ITER, dklen=KEY_LEN)

def make_user_hash(password: str) -> str:
    """Return 'saltB64:hashB64' string for storage."""
    salt = os.urandom(SALT_LEN)
    h = pbkdf2_hash(password, salt)
    return base64.b64encode(salt).decode('ascii') + ":" + base64.b64encode(h).decode('ascii')

# Pre-computed fixed-salt users so this script produces reproducible hashes.
# This way the script can be re-run without changing the stored password.
USERS = [
    ("user-admin-001",     "admin",     "admin123",       "System Administrator",  "ADMINISTRATOR",  "+91 9876543210",  "admin@mahallu.app"),
    ("user-secretary-001",  "secretary", "secretary123",   "Shahid K",              "SECRETARY",      "+91 9876501234",  "secretary@mahallu.app"),
    ("user-treasurer-001",  "treasurer", "treasurer123",   "Ibrahim Kutty",         "TREASURER",      "+91 9876512345",  "treasurer@mahallu.app"),
]

# Fixed salt per user so the output is reproducible across runs
# (We use SHA-256 of "mahallu-{username}-fixed" as the salt)
def fixed_salt(username: str) -> bytes:
    return hashlib.sha256(f"mahallu-{username}-fixed-salt-v1".encode('utf-8')).digest()[:SALT_LEN]

def make_user_hash_fixed(password: str, username: str) -> str:
    salt = fixed_salt(username)
    h = pbkdf2_hash(password, salt)
    return base64.b64encode(salt).decode('ascii') + ":" + base64.b64encode(h).decode('ascii')

def build_sql() -> str:
    """Build the SQL to create + insert users."""
    lines = []
    lines.append("CREATE TABLE IF NOT EXISTS users (id TEXT PRIMARY KEY NOT NULL, username TEXT NOT NULL, passwordHash TEXT NOT NULL, fullName TEXT NOT NULL, role TEXT NOT NULL, phone TEXT, email TEXT, isActive INTEGER NOT NULL DEFAULT 1, createdAt INTEGER NOT NULL, lastLoginAt INTEGER);")
    lines.append("CREATE UNIQUE INDEX IF NOT EXISTS index_users_username ON users (username);")
    now = 1700000000000  # fixed timestamp
    for (uid, uname, pwd, fullname, role, phone, email) in USERS:
        ph = make_user_hash_fixed(pwd, uname).replace("'", "''")
        fn = fullname.replace("'", "''")
        un = uname.replace("'", "''")
        em = email.replace("'", "''")
        lines.append(f"INSERT OR REPLACE INTO users (id, username, passwordHash, fullName, role, phone, email, isActive, createdAt) VALUES ('{uid}','{un}','{ph}','{fn}','{role}','{phone}','{em}',1,{now});")
    return "\n".join(lines)

def main():
    if "--print" in sys.argv:
        print(build_sql())
        return
    if "--local" in sys.argv:
        # For local testing only
        out = build_sql()
        print(out)
        with open("/tmp/mahallu_seed.sql", "w") as f:
            f.write(out)
        return

    sql = build_sql()
    # Write SQL to a file in /tmp
    sql_path = "/tmp/mahallu_seed.sql"
    with open(sql_path, "w") as f:
        f.write(sql)

    # Use adb to push the SQL to the device, then run it via run-as
    adb = "adb"
    pkg = "com.mahallu.manager.debug"

    print("Mahallu Manager - Database Seeder")
    print("==================================")
    print()
    print(f"Package: {pkg}")
    print(f"SQL length: {len(sql)} bytes")
    print()

    # Check adb available
    try:
        out = subprocess.check_output([adb, "version"], stderr=subprocess.STDOUT, text=True, timeout=5)
        print(f"adb: {out.strip().splitlines()[0]}")
    except Exception as e:
        print(f"ERROR: adb not available: {e}")
        print("\nFallback steps:")
        print("1. Enable USB debugging on your phone (Settings > About > tap build# 7x > Developer options > USB debugging)")
        print("2. Connect phone via USB cable")
        print("3. Run: adb devices   (should list your device)")
        print("4. Re-run this script")
        sys.exit(1)

    # Check device
    try:
        devices = subprocess.check_output([adb, "devices"], text=True, timeout=10).strip()
        if "device" not in devices.split("\n")[1:][0] if len(devices.split("\n")) > 1 else True:
            pass
    except Exception:
        pass
    print(subprocess.check_output([adb, "devices"], text=True, timeout=10))

    # Push SQL to device
    remote_path = "/data/local/tmp/mahallu_seed.sql"
    print(f"\nPushing SQL to {remote_path}...")
    res = subprocess.run([adb, "push", sql_path, remote_path], capture_output=True, text=True)
    print(res.stdout)
    if res.returncode != 0:
        print("Push failed. Is your phone connected via USB with USB debugging enabled?")
        sys.exit(1)

    # Run SQL via run-as (only works for debug-signed APKs)
    print(f"Executing SQL inside the app's data dir (package={pkg})...")
    res = subprocess.run(
        [adb, "shell", f"run-as {pkg} sh -c 'cat /data/local/tmp/mahallu_seed.sql | sqlite3 /data/data/{pkg}/databases/mahallu_db'"],
        capture_output=True, text=True
    )
    print("stdout:", res.stdout)
    print("stderr:", res.stderr)

    # Verify
    print("\nVerifying users...")
    res = subprocess.run(
        [adb, "shell", f"run-as {pkg} sqlite3 /data/data/{pkg}/databases/mahallu_db 'SELECT id, username, role FROM users;'"],
        capture_output=True, text=True
    )
    print("Users in database:")
    print(res.stdout)
    if res.stderr:
        print("stderr:", res.stderr)

    print()
    print("=" * 50)
    print("DONE!")
    print("=" * 50)
    print("Now open Mahallu Manager on your phone and log in with:")
    print("  admin / admin123")
    print("  secretary / secretary123")
    print("  treasurer / treasurer123")

if __name__ == "__main__":
    main()
