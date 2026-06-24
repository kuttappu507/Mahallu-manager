package com.mahallu.manager.domain.usecase.finance

import com.mahallu.manager.domain.model.Finance
import com.mahallu.manager.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(): Flow<List<Finance>> {
        return repository.getAllTransactions()
    }
}

class GetTransactionByIdUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(id: Int): Finance? {
        return repository.getTransactionById(id)
    }
}

class SaveTransactionUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(transaction: Finance) {
        if (transaction.id == 0) {
            repository.insertTransaction(transaction)
        } else {
            repository.updateTransaction(transaction)
        }
    }
}

class DeleteTransactionUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(transaction: Finance) {
        repository.deleteTransaction(transaction)
    }
}

class GetIncomeByCategoryUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(category: String): Flow<List<Finance>> {
        return repository.getIncomeByCategory(category)
    }
}

class GetExpenseByCategoryUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(category: String): Flow<List<Finance>> {
        return repository.getExpenseByCategory(category)
    }
}

class GetMonthlyIncomeUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Double {
        return repository.getMonthlyIncome(year, month)
    }
}

class GetMonthlyExpenseUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Double {
        return repository.getMonthlyExpense(year, month)
    }
}

class GetYearlySummaryUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(year: Int): Pair<Double, Double> {
        return repository.getYearlySummary(year)
    }
}

class GetCashbookUseCase @Inject constructor(
    private val repository: FinanceRepository
) {
    operator fun invoke(): Flow<List<Finance>> {
        return repository.getCashbook()
    }
}
