package com.mahallu.manager.domain.model

import java.util.Date

data class Donation(
    val id: Int = 0,
    val donorName: String,
    val amount: Double,
    val purpose: DonationCategory,
    val date: Date,
    val remarks: String? = null,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class DonationCategory {
    GENERAL,
    MASJID,
    BUILDING_FUND,
    EDUCATION_FUND,
    MEDICAL_FUND,
    WELFARE,
    OTHER
}
