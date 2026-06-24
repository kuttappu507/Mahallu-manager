package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "finance")
data class Finance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: FinanceType,
    val category: FinanceCategory,
    val amount: Double,
    val date: Date,
    val description: String,
    val referenceNumber: String?,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val createdBy: Long?,
    val createdAt: Date = Date()
)

enum class FinanceType { INCOME, EXPENSE }

enum class FinanceCategory {
    // Income categories
    SUBSCRIPTION, DONATION, RENT, OTHER_INCOME,
    // Expense categories
    SALARY, ELECTRICITY, WATER, MAINTENANCE, WELFARE, OTHER_EXPENSE
}
