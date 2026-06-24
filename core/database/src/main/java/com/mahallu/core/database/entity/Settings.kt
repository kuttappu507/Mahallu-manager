package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val mahalluName: String,
    val address: String?,
    val phone: String?,
    val email: String?,
    val logoUri: String?,
    val sealUri: String?,
    val subscriptionAmount: Double = 0.0,
    val currencySymbol: String = "₹",
    val backupEnabled: Boolean = true,
    val backupFrequency: BackupFrequency = BackupFrequency.DAILY,
    val lastBackupDate: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BackupFrequency { DAILY, WEEKLY, MONTHLY }
