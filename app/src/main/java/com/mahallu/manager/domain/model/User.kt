package com.mahallu.manager.domain.model

import java.util.Date

data class User(
    val id: Int = 0,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val isActive: Boolean = true,
    val lastLoginAt: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
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
