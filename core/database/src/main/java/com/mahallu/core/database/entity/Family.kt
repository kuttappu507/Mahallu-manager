package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "families")
data class Family(
    @PrimaryKey val id: Long = 0,
    val familyNumber: String,
    val houseName: String?,
    val houseNumber: String?,
    val ward: String?,
    val area: String?,
    val address: String,
    val pincode: String?,
    val primaryMobile: String,
    val secondaryMobile: String?,
    val status: FamilyStatus = FamilyStatus.ACTIVE,
    val photoPath: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)

enum class FamilyStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
