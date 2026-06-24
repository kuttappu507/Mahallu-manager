package com.mahallu.manager.domain.model

import java.util.Date

data class Family(
    val id: Int = 0,
    val familyNumber: String,
    val houseName: String?,
    val houseNumber: String?,
    val ward: String,
    val area: String,
    val address: String,
    val pincode: String,
    val primaryMobile: String,
    val secondaryMobile: String?,
    val status: FamilyStatus = FamilyStatus.ACTIVE,
    val photoUri: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class FamilyStatus {
    ACTIVE,
    ARCHIVED,
    INACTIVE
}

data class FamilyWithMembers(
    val family: Family,
    val members: List<Member>
)

data class FamilyWithMemberCount(
    val family: Family,
    val memberCount: Int
)
