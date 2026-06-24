package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "families",
    indices = [Index(value = ["familyNumber"], unique = true)]
)
data class FamilyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
    val isArchived: Boolean = false,
    val archivedAt: Long? = null
)

enum class FamilyStatus {
    ACTIVE,
    INACTIVE,
    MOVED,
    DECEASED
}
