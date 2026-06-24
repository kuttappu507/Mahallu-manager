package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val name: String,
    val email: String?,
    val mobile: String?,
    val isActive: Boolean = true,
    val lastLoginAt: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class UserRole {
    ADMINISTRATOR, PRESIDENT, SECRETARY, TREASURER, IMAM, STAFF, AUDITOR
}
