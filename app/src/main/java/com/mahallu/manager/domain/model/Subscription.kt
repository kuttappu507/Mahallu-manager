package com.mahallu.manager.domain.model

import java.util.Date

data class Subscription(
    val id: Int = 0,
    val familyId: Int,
    val amount: Double,
    val date: Date,
    val receiptNumber: String,
    val paymentMethod: PaymentMethod,
    val subscriptionType: SubscriptionType,
    val remarks: String? = null,
    val month: Int,
    val year: Int,
    val isPaid: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class PaymentMethod {
    CASH,
    CARD,
    UPI,
    BANK_TRANSFER,
    CHEQUE
}

enum class SubscriptionType {
    MONTHLY,
    YEARLY,
    SPECIAL
}

data class SubscriptionWithFamily(
    val subscription: Subscription,
    val family: Family
)
