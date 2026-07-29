# Utility Scripts

## seed_database.py

**Only needed if you're running an older build of Mahallu Manager that doesn't auto-seed the database.**

The current v1.0.0+ builds include automatic seed data. This script is a workaround for older APKs that shipped without the `SeedData.seedIfEmpty()` call.

### Usage
```bash
# 1. Connect phone with USB debugging on
# 2. Open the Mahallu Manager app once (creates the empty database)
# 3. Run the script:
python3 seed_database.py
```

The script will:
1. Generate PBKDF2 password hashes matching the app's expected format
2. Push the SQL to the device via adb
3. Execute it via `run-as` to write to the app's SQLite database
4. Verify by reading back the users

After running, log in with:
- `admin / admin123`
- `secretary / secretary123`
- `treasurer / treasurer123`
