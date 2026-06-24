package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "donations",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("familyId"), Index("memberId")]
)
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long?,
    val memberId: Long?,
    val donorName: String,
    val amount: Double,
    val purpose: DonationPurpose,
    val date: Date,
    val remarks: String?,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val createdAt: Date = Date()
)

enum class DonationPurpose {
    GENERAL, MASJID, BUILDING_FUND, EDUCATION_FUND, MEDICAL_FUND, WELFARE, OTHER
}
