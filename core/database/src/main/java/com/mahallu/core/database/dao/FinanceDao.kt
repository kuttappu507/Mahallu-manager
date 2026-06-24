package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Finance
import com.mahallu.core.database.entity.TransactionType
import com.mahallu.core.database.entity.FinanceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Finance>>

    @Query("SELECT * FROM finance WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Finance>>

    @Query("SELECT * FROM finance WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: FinanceCategory): Flow<List<Finance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(finance: Finance): Long

    @Update
    suspend fun updateTransaction(finance: Finance)

    @Delete
    suspend fun deleteTransaction(finance: Finance)

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'INCOME' AND strftime('%Y', date) = :year")
    suspend fun getTotalIncomeByYear(year: Int): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'EXPENSE' AND strftime('%Y', date) = :year")
    suspend fun getTotalExpenseByYear(year: Int): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'INCOME' AND strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTotalIncomeByMonth(month: String, year: Int): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'EXPENSE' AND strftime('%m', date) = :month AND strftime('%Y', date) = :year")
    suspend fun getTotalExpenseByMonth(month: String, year: Int): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'INCOME'")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'EXPENSE'")
    suspend fun getTotalExpense(): Double?
}
