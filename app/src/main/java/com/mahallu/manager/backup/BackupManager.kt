package com.mahallu.manager.backup

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mahallu.manager.core.database.MahalluDatabase
import com.mahallu.manager.core.database.entity.BackupEntity
import com.mahallu.manager.core.database.repository.BackupRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.security.AesGcmCipher
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class BackupOutcome(
    val success: Boolean,
    val backupId: String,
    val message: String,
    val sizeBytes: Long = 0
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: MahalluDatabase,
    private val backupRepo: BackupRepository,
    private val settingsRepo: SettingsRepository,
    private val driveManager: GoogleDriveManager
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    suspend fun performBackup(isManual: Boolean = false): BackupOutcome = withContext(Dispatchers.IO) {
        val backupId = IdGenerator.backupId()
        val pending = BackupEntity(
            id = backupId,
            fileName = "$backupId.mhlbak",
            status = "IN_PROGRESS",
            type = if (isManual) "MANUAL" else "AUTO",
            size = 0
        )
        backupRepo.save(pending)

        try {
            val payload = buildJsonObject {
                put("version", JsonPrimitive(1))
                put("exportedAt", JsonPrimitive(System.currentTimeMillis()))
                put("mahalluName", JsonPrimitive("Mahallu Manager Backup"))
                put("users", jsonArrayFromTable(db.openHelper.readableDatabase, "users"))
                put("families", jsonArrayFromTable(db.openHelper.readableDatabase, "families"))
                put("members", jsonArrayFromTable(db.openHelper.readableDatabase, "members"))
                put("subscriptions", jsonArrayFromTable(db.openHelper.readableDatabase, "subscriptions"))
                put("donations", jsonArrayFromTable(db.openHelper.readableDatabase, "donations"))
                put("finance", jsonArrayFromTable(db.openHelper.readableDatabase, "finance_entries"))
                put("marriages", jsonArrayFromTable(db.openHelper.readableDatabase, "marriages"))
                put("deaths", jsonArrayFromTable(db.openHelper.readableDatabase, "deaths"))
                put("welfare", jsonArrayFromTable(db.openHelper.readableDatabase, "welfare_requests"))
                put("certificates", jsonArrayFromTable(db.openHelper.readableDatabase, "certificates"))
                put("settings", jsonArrayFromTable(db.openHelper.readableDatabase, "settings"))
            }

            val payloadJson = json.encodeToString(JsonObject.serializer(), payload)
            val payloadBytes = payloadJson.toByteArray(Charsets.UTF_8)
            val zipBytes = zipBytes("backup.json", payloadBytes)
            val masterKey = ensureMasterKey()
            val encrypted = AesGcmCipher.encrypt(zipBytes, masterKey)

            val backupDir = File(context.getExternalFilesDir(null), "backups").apply { mkdirs() }
            val file = File(backupDir, pending.fileName)
            FileOutputStream(file).use { it.write(encrypted) }
            val size = file.length()
            val checksum = sha256(encrypted)

            val (driveFileId, driveLink) = try {
                driveManager.upload(pending.fileName, encrypted)
            } catch (t: Throwable) {
                Timber.w(t, "Drive upload skipped")
                Pair(null as String?, null as String?)
            }

            backupRepo.save(
                pending.copy(
                    localPath = file.absolutePath,
                    driveFileId = driveFileId,
                    driveLink = driveLink,
                    size = size,
                    status = "SUCCESS",
                    checksum = checksum
                )
            )
            settingsRepo.putLong("backup.last_at", System.currentTimeMillis())
            enforceRetention(30)

            BackupOutcome(true, backupId, "Backup completed (${size / 1024} KB)", size)
        } catch (t: Throwable) {
            Timber.e(t, "Backup failed")
            backupRepo.save(pending.copy(status = "FAILED", message = t.message))
            BackupOutcome(false, backupId, t.message ?: "Unknown error")
        }
    }

    suspend fun restoreBackup(backupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backups = backupRepo.allSuccessful()
            val backup = backups.firstOrNull { it.id == backupId }
                ?: return@withContext Result.failure(IllegalStateException("Backup not found"))

            val local = backup.localPath?.let { File(it) }
                ?: return@withContext Result.failure(IllegalStateException("Local backup file missing"))

            val encrypted = FileInputStream(local).use { it.readBytes() }
            val masterKey = ensureMasterKey()
            val decrypted = AesGcmCipher.decrypt(encrypted, masterKey)
            val (name, content) = unzipFirst(decrypted)
            if (name != "backup.json") return@withContext Result.failure(IllegalStateException("Invalid backup"))

            val payload = json.parseToJsonElement(String(content, Charsets.UTF_8)).jsonObject
            restorePayload(payload)

            backupRepo.save(backup.copy(status = "RESTORED", message = "Restored at ${System.currentTimeMillis()}"))
            Result.success(Unit)
        } catch (t: Throwable) {
            Timber.e(t, "Restore failed")
            Result.failure(t)
        }
    }

    private fun ensureMasterKey(): ByteArray {
        val prefs = context.getSharedPreferences("mahallu_secure_prefs", Context.MODE_PRIVATE)
        val existing = prefs.getString("backup_master_key", null)
        if (existing != null) return AesGcmCipher.fromBase64(existing)
        val newKey = AesGcmCipher.generateKey()
        prefs.edit().putString("backup_master_key", AesGcmCipher.toBase64(newKey)).apply()
        return newKey
    }

    private fun restorePayload(payload: JsonObject) {
        val db = this.db.openHelper.writableDatabase
        db.beginTransaction()
        try {
            val tables = listOf(
                "users", "families", "members", "subscriptions", "donations",
                "finance_entries", "marriages", "deaths", "welfare_requests",
                "certificates", "settings"
            )
            for (table in tables) {
                val items = payload[table]?.jsonArray ?: continue
                db.execSQL("DELETE FROM $table")
                for (item in items) {
                    val obj = item.jsonObject
                    val columns = obj.keys.joinToString(",")
                    val placeholders = obj.keys.joinToString(",") { "?" }
                    val values = obj.keys.map { key ->
                        val v = obj[key]
                        if (v is JsonPrimitive) {
                            if (v.isString) v.content else v.content
                        } else null
                    }.toTypedArray()
                    try {
                        db.execSQL("INSERT INTO $table ($columns) VALUES ($placeholders)", values)
                    } catch (t: Throwable) {
                        Timber.w(t, "Skip row in $table")
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun jsonArrayFromTable(
        db: SupportSQLiteDatabase,
        table: String
    ): JsonArray {
        val list = mutableListOf<JsonElement>()
        db.query("SELECT * FROM $table").use { cursor ->
            val cols = cursor.columnNames
            while (cursor.moveToNext()) {
                val obj = buildJsonObject {
                    for (col in cols) {
                        val idx = cursor.getColumnIndexOrThrow(col)
                        val value: Any? = when (cursor.getType(idx)) {
                            android.database.Cursor.FIELD_TYPE_NULL -> null
                            android.database.Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(idx)
                            android.database.Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(idx)
                            android.database.Cursor.FIELD_TYPE_STRING -> cursor.getString(idx)
                            else -> cursor.getString(idx)
                        }
                        put(col, if (value == null) JsonNull else JsonPrimitive(value.toString()))
                    }
                }
                list.add(obj)
            }
        }
        return JsonArray(list)
    }

    private fun zipBytes(entryName: String, data: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            zip.putNextEntry(ZipEntry(entryName))
            zip.write(data)
            zip.closeEntry()
        }
        return out.toByteArray()
    }

    private fun unzipFirst(data: ByteArray): Pair<String, ByteArray> {
        val zin = ZipInputStream(data.inputStream())
        val entry = zin.nextEntry ?: return "" to ByteArray(0)
        val out = ByteArrayOutputStream()
        zin.copyTo(out)
        return entry.name to out.toByteArray()
    }

    private fun sha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(data)
        return digest.joinToString("") { "%02x".format(it) }
    }

    private suspend fun enforceRetention(keepLast: Int) {
        val all = backupRepo.allSuccessful()
        if (all.size <= keepLast) return
        val toDelete = all.drop(keepLast)
        toDelete.forEach { b ->
            b.localPath?.let { runCatching { File(it).delete() } }
            backupRepo.delete(b.id)
        }
    }
}