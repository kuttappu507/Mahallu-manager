package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.FinanceTransaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getAllTransactions(): Flow<List<FinanceTransaction>>
    fun getTransactionById(id: Int): Flow<FinanceTransaction?>
    suspend fun insertTransaction(transaction: FinanceTransaction)
    suspend fun updateTransaction(transaction: FinanceTransaction)
    suspend fun deleteTransaction(transaction: FinanceTransaction)
    fun getIncomeTransactions(): Flow<List<FinanceTransaction>>
    fun getExpenseTransactions(): Flow<List<FinanceTransaction>>
    fun getTransactionsByCategory(category: String): Flow<List<FinanceTransaction>>
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<FinanceTransaction>>
    suspend fun getTotalIncome(): Double
    suspend fun getTotalExpense(): Double
    suspend fun getBalance(): Double
}
