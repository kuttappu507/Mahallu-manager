package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "members",
    indices = [
        Index(value = ["familyId"]),
        Index(value = ["memberId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: String, // Unique member ID
    val familyId: Long,
    val name: String,
    val arabicName: String?,
    val gender: Gender,
    val dateOfBirth: Long?, // Timestamp
    val age: Int?,
    val occupation: String?,
    val education: String?,
    val bloodGroup: String?,
    val maritalStatus: MaritalStatus,
    val mobile: String?,
    val email: String?,
    val nationality: String?,
    val address: String?,
    val emergencyContact: String?,
    val emergencyContactName: String?,
    val photoPath: String?,
    val qrCodePath: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Gender {
    MALE,
    FEMALE
}

enum class MaritalStatus {
    SINGLE,
    MARRIED,
    DIVORCED,
    WIDOWED
}
