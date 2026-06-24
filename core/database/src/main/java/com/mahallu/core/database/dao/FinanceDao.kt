package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Income
import com.mahallu.core.database.entity.Expense
import com.mahallu.core.database.entity.IncomeCategory
import com.mahallu.core.database.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAllIncomes(): Flow<List<Income>>
    
    @Query("SELECT * FROM incomes WHERE id = :id")
    suspend fun getIncomeById(id: Long): Income?
    
    @Query("SELECT * FROM incomes WHERE category = :category ORDER BY date DESC")
    fun getIncomesByCategory(category: IncomeCategory): Flow<List<Income>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(income: Income): Long
    
    @Update
    suspend fun update(income: Income)
    
    @Delete
    suspend fun delete(income: Income)
    
    @Query("SELECT SUM(amount) FROM incomes WHERE year(date/1000) = :year AND month(date/1000) = :month")
    fun getTotalByMonth(year: Int, month: Int): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM incomes WHERE year(date/1000) = :year")
    fun getTotalByYear(year: Int): Flow<Double?>
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?
    
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: ExpenseCategory): Flow<List<Expense>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long
    
    @Update
    suspend fun update(expense: Expense)
    
    @Delete
    suspend fun delete(expense: Expense)
    
    @Query("SELECT SUM(amount) FROM expenses WHERE year(date/1000) = :year AND month(date/1000) = :month")
    fun getTotalByMonth(year: Int, month: Int): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE year(date/1000) = :year")
    fun getTotalByYear(year: Int): Flow<Double?>
}
