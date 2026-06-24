package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"]),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"])
    ],
    indices = [Index("familyId"), Index("memberId")]
)
data class Subscription(
    @PrimaryKey val id: Long = 0,
    val familyId: Long?,
    val memberId: Long?,
    val type: SubscriptionType,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val month: Int,
    val year: Int,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SubscriptionType { MONTHLY, YEARLY, SPECIAL }
enum class PaymentMethod { CASH, CARD, UPI, BANK_TRANSFER, CHEQUE }
