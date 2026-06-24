package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val donorName: String,
    val amount: Double,
    val purpose: DonationPurpose,
    val date: Date,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val familyId: Long?,
    val memberId: Long?,
    val createdAt: Date = Date()
)

enum class DonationPurpose {
    GENERAL, MASJID, BUILDING_FUND, EDUCATION_FUND, MEDICAL_FUND, WELFARE, OTHER
}
