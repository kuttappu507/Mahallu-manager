package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

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
    indices = [Index(value = ["familyId"])]
)
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long,
    val name: String,
    val arabicName: String?,
    val gender: Gender,
    val dateOfBirth: Date?,
    val occupation: String?,
    val education: String?,
    val bloodGroup: String?,
    val maritalStatus: MaritalStatus,
    val mobile: String?,
    val email: String?,
    val nationality: String?,
    val address: String?,
    val emergencyContact: String?,
    val photoUri: String?,
    val isPrimary: Boolean = false,
    val status: MemberStatus = MemberStatus.ACTIVE,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class Gender { MALE, FEMALE }
enum class MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED }
enum class MemberStatus { ACTIVE, INACTIVE, DECEASED }
