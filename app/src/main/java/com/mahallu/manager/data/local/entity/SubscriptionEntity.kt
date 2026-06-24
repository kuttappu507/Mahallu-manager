package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    indices = [
        Index(value = ["familyId"]),
        Index(value = ["receiptNumber"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val familyId: Long,
    val amount: Double,
    val subscriptionType: SubscriptionType,
    val receiptNumber: String,
    val paymentDate: Long,
    val paymentMethod: PaymentMethod,
    val remarks: String?,
    val isPaid: Boolean = true,
    val recordedBy: Long, // User ID
    val createdAt: Long = System.currentTimeMillis()
)

enum class SubscriptionType {
    MONTHLY,
    YEARLY,
    SPECIAL_COLLECTION
}

enum class PaymentMethod {
    CASH,
    CARD,
    UPI,
    BANK_TRANSFER,
    CHEQUE
}
