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
    val website: String?,
    val subscriptionAmount: Double = 0.0,
    val yearlySubscriptionAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
