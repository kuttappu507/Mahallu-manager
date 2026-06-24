package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val donorName: String,
    val amount: Double,
    val purpose: DonationPurpose,
    val donationDate: Long,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val isAnonymous: Boolean = false,
    val recordedBy: Long, // User ID
    val createdAt: Long = System.currentTimeMillis()
)

enum class DonationPurpose {
    GENERAL_DONATION,
    MASJID_DONATION,
    BUILDING_FUND,
    EDUCATION_FUND,
    MEDICAL_FUND,
    WELFARE_FUND,
    OTHER
}
