package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity

/**
 * Builds the income [FinanceEntryEntity] that mirrors a donation or a
 * subscription collection, so the Income-vs-Expense screen reflects them.
 * The id is derived from the source id so re-saving updates (never duplicates)
 * and delete can remove it via [receiptId].
 */
fun financeEntryFromDonation(d: DonationEntity): FinanceEntryEntity = FinanceEntryEntity(
    id = "fin-$d.id",
    type = "INCOME",
    category = "DONATION",
    amount = d.amount,
    date = d.date,
    description = d.donorName,
    paymentMethod = d.paymentMethod,
    reference = d.receiptNumber,
    receiptId = d.id,
    createdAt = d.createdAt
)

fun financeEntryFromSubscription(s: SubscriptionEntity): FinanceEntryEntity = FinanceEntryEntity(
    id = "fin-$s.id",
    type = "INCOME",
    category = "SUBSCRIPTION",
    amount = s.amount,
    date = s.date,
    description = s.remarks ?: "Subscription ${s.receiptNumber}",
    paymentMethod = s.paymentMethod,
    reference = s.receiptNumber,
    receiptId = s.id,
    createdAt = s.createdAt
)
