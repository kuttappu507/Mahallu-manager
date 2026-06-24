package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Finance
import com.mahallu.core.database.entity.FinanceCategory
import com.mahallu.core.database.entity.FinanceType
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Finance>>

    @Query("SELECT * FROM finance WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: FinanceType): Flow<List<Finance>>

    @Query("SELECT * FROM finance WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: FinanceCategory): Flow<List<Finance>>

    @Query("SELECT * FROM finance WHERE id = :id")
    suspend fun getTransactionById(id: Long): Finance?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'INCOME' AND category = :category AND date BETWEEN :startDate AND :endDate")
    suspend fun getIncomeByCategory(category: FinanceCategory, startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amount) FROM finance WHERE type = 'EXPENSE' AND category = :category AND date BETWEEN :startDate AND :endDate")
    suspend fun getExpenseByCategory(category: FinanceCategory, startDate: Long, endDate: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(finance: Finance): Long

    @Update
    suspend fun updateTransaction(finance: Finance)

    @Delete
    suspend fun deleteTransaction(finance: Finance)
}
