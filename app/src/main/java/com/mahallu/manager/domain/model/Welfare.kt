package com.mahallu.manager.domain.model

import java.util.Date

data class WelfareRequest(
    val id: Int = 0,
    val applicantName: String,
    val familyId: Int?,
    val category: WelfareCategory,
    val amount: Double,
    val reason: String,
    val status: WelfareStatus = WelfareStatus.PENDING,
    val requestedDate: Date = Date(),
    val approvedDate: Date? = null,
    val disbursedDate: Date? = null,
    val remarks: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class WelfareCategory {
    MEDICAL_AID,
    EDUCATION_AID,
    MARRIAGE_ASSISTANCE,
    FINANCIAL_ASSISTANCE,
    OTHER
}

enum class WelfareStatus {
    PENDING,
    APPROVED,
    REJECTED,
    DISBURSED
}
