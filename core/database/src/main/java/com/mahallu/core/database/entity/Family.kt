package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "families")
data class Family(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val photoUri: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class FamilyStatus {
    ACTIVE, ARCHIVED, INACTIVE
}
