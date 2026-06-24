package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.TransactionEntity
import com.mahallu.manager.data.local.entity.TransactionType
import com.mahallu.manager.data.local.entity.TransactionCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY transactionDate DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY transactionDate DESC")
    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND strftime('%m', transactionDate/1000, 'unixepoch') = strftime('%m', 'now')")
    fun getCurrentMonthIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND strftime('%m', transactionDate/1000, 'unixepoch') = strftime('%m', 'now')")
    fun getCurrentMonthExpense(): Flow<Double?>

    @Query("SELECT SUM(amount) - (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE') as balance FROM transactions WHERE type = 'INCOME'")
    fun getBalance(): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE transactionDate >= :startDate AND transactionDate <= :endDate ORDER BY transactionDate DESC")
    fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'INCOME' GROUP BY category")
    fun getIncomeByCategory(): Flow<Map<String, Double>>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    fun getExpenseByCategory(): Flow<Map<String, Double>>

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC LIMIT 20")
    fun getRecentTransactions(): Flow<List<TransactionEntity>>
}
