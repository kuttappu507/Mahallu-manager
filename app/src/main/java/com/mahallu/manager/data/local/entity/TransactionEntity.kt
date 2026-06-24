package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val description: String,
    val transactionDate: Long,
    val referenceNumber: String?,
    val paymentMethod: PaymentMethod?,
    val recordedBy: Long, // User ID
    val remarks: String?,
    val createdAt: Long = System.currentTimeMillis()
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
