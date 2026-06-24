package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey val id: Long = 0,
    val amount: Double,
    val category: IncomeCategory,
    val date: Long = System.currentTimeMillis(),
    val description: String?,
    val receiptNumber: String?,
    val paymentMethod: PaymentMethod,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: Long = 0,
    val amount: Double,
    val category: ExpenseCategory,
    val date: Long = System.currentTimeMillis(),
    val description: String?,
    val recipientName: String?,
    val paymentMethod: PaymentMethod,
    val createdAt: Long = System.currentTimeMillis()
)

enum class IncomeCategory { SUBSCRIPTION, DONATION, RENT, OTHER }
enum class ExpenseCategory { SALARY, ELECTRICITY, WATER, MAINTENANCE, WELFARE, OTHER }
