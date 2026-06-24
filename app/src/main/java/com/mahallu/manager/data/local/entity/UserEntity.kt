package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)

enum class UserRole {
    ADMINISTRATOR,
    PRESIDENT,
    SECRETARY,
    TREASURER,
    IMAM,
    STAFF,
    AUDITOR
}
