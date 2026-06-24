package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"]),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"])
    ],
    indices = [Index(value = ["familyId"]), Index(value = ["memberId"])]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long?,
    val memberId: Long?,
    val type: SubscriptionType,
    val amount: Double,
    val date: Date,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val month: Int?,
    val year: Int,
    val createdAt: Date = Date()
)

enum class SubscriptionType { MONTHLY, YEARLY, SPECIAL }
enum class PaymentMethod { CASH, CARD, UPI, BANK_TRANSFER, CHEQUE }
