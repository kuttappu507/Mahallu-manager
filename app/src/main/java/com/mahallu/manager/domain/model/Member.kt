package com.mahallu.manager.domain.model

import java.util.Date

data class Member(
    val id: Int = 0,
    val familyId: Int,
    val name: String,
    val arabicName: String?,
    val gender: Gender,
    val dateOfBirth: Date?,
    val age: Int,
    val occupation: String?,
    val education: String?,
    val bloodGroup: String?,
    val maritalStatus: MaritalStatus,
    val mobile: String?,
    val email: String?,
    val nationality: String?,
    val address: String?,
    val emergencyContact: String?,
    val photoUri: String? = null,
    val isPrimary: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
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

data class MemberWithFamily(
    val member: Member,
    val family: Family
)
