package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("familyId"), Index("memberId")]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long?,
    val memberId: Long?,
    val amount: Double,
    val date: Date,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val subscriptionType: SubscriptionType = SubscriptionType.MONTHLY,
    val status: PaymentStatus = PaymentStatus.PAID,
    val createdAt: Date = Date()
)

enum class SubscriptionType { MONTHLY, YEARLY, SPECIAL }
enum class PaymentMethod { CASH, CARD, UPI, BANK_TRANSFER, CHEQUE }
enum class PaymentStatus { PAID, PENDING, OVERDUE, CANCELLED }
