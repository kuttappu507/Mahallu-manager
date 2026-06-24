package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mahalluName: String,
    val mahalluAddress: String,
    val mahalluPhone: String,
    val mahalluEmail: String?,
    val logoPath: String?,
    val sealPath: String?,
    val backupEnabled: Boolean = true,
    val backupFrequency: BackupFrequency = BackupFrequency.DAILY,
    val lastBackupAt: Long? = null,
    val theme: AppTheme = AppTheme.LIGHT,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    MANUAL_ONLY
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
