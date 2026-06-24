package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Long = 0,
    val mahalluName: String,
    val address: String?,
    val phone: String?,
    val email: String?,
    val logoPath: String?,
    val sealPath: String?,
    val subscriptionAmount: Double = 100.0,
    val currency: String = "INR",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "backup_logs")
data class BackupLog(
    @PrimaryKey val id: Long = 0,
    val backupPath: String,
    val backupType: BackupType,
    val status: BackupStatus,
    val fileSize: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BackupType { MANUAL, AUTO }
enum class BackupStatus { PENDING, IN_PROGRESS, COMPLETED, FAILED }
