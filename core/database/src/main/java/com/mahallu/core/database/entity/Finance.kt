package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "finance")
data class Finance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val category: FinanceCategory,
    val amount: Double,
    val date: Date,
    val description: String,
    val referenceId: Long?,
    val referenceType: ReferenceType?,
    val paymentMethod: PaymentMethod,
    val receiptNumber: String?,
    val createdAt: Date = Date()
)

enum class TransactionType { INCOME, EXPENSE }
enum class FinanceCategory {
    SUBSCRIPTION, DONATION, RENT, OTHER_INCOME, SALARY, ELECTRICITY, WATER, MAINTENANCE, WELFARE, OTHER_EXPENSE
}
enum class ReferenceType { SUBSCRIPTION, DONATION, FAMILY, MEMBER }
