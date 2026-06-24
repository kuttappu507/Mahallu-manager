package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey val id: Long = 0,
    val donorName: String,
    val amount: Double,
    val purpose: DonationPurpose,
    val date: Long = System.currentTimeMillis(),
    val remarks: String?,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DonationPurpose {
    GENERAL, MASJID, BUILDING_FUND, EDUCATION_FUND, MEDICAL_FUND, WELFARE, OTHER
}
