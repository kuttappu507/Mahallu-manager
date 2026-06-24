package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.FinanceDao
import com.mahallu.manager.domain.model.FinanceTransaction
import com.mahallu.manager.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val financeDao: FinanceDao
) : FinanceRepository {

    override fun getAllTransactions(): Flow<List<FinanceTransaction>> {
        return financeDao.getAllTransactions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTransactionById(id: Int): Flow<FinanceTransaction?> {
        return financeDao.getTransactionById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertTransaction(transaction: FinanceTransaction) {
        financeDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: FinanceTransaction) {
        financeDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: FinanceTransaction) {
        financeDao.deleteTransaction(transaction.toEntity())
    }

    override fun getIncomeTransactions(): Flow<List<FinanceTransaction>> {
        return financeDao.getIncomeTransactions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExpenseTransactions(): Flow<List<FinanceTransaction>> {
        return financeDao.getExpenseTransactions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTransactionsByCategory(category: String): Flow<List<FinanceTransaction>> {
        return financeDao.getTransactionsByCategory(category).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<FinanceTransaction>> {
        return financeDao.getTransactionsByDateRange(startDate, endDate).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalIncome(): Double {
        return financeDao.getTotalIncome()
    }

    override suspend fun getTotalExpense(): Double {
        return financeDao.getTotalExpense()
    }

    override suspend fun getBalance(): Double {
        return financeDao.getBalance()
    }
}
