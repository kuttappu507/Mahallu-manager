package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = Family::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("familyId")]
)
data class Member(
    @PrimaryKey val id: Long = 0,
    val familyId: Long,
    val name: String,
    val arabicName: String?,
    val gender: Gender,
    val dateOfBirth: Long?,
    val occupation: String?,
    val education: String?,
    val bloodGroup: String?,
    val maritalStatus: MaritalStatus,
    val mobile: String?,
    val email: String?,
    val nationality: String?,
    val address: String?,
    val emergencyContact: String?,
    val photoPath: String?,
    val qrCodePath: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Gender { MALE, FEMALE }
enum class MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED }
