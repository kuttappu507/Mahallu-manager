package com.mahallu.manager.domain.model

import java.util.Date

data class FinanceTransaction(
    val id: Int = 0,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val date: Date,
    val description: String,
    val referenceNumber: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory {
    // Income categories
    SUBSCRIPTION,
    DONATION,
    RENT,
    OTHER_INCOME,
    
    // Expense categories
    SALARY,
    ELECTRICITY,
    WATER,
    MAINTENANCE,
    WELFARE,
    OTHER_EXPENSE
}

data class MonthlyFinanceSummary(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)

data class YearlyFinanceSummary(
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val monthlySummaries: List<MonthlyFinanceSummary>
)
