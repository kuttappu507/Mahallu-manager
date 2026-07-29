package com.mahallu.manager.feature.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.repository.FinanceRepository
import com.mahallu.manager.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FinanceUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val entries: List<FinanceEntryEntity> = emptyList(),
    val typeFilter: String = "ALL",
    val isLoading: Boolean = true
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repo: FinanceRepository
) : ViewModel() {

    private val typeFilter = MutableStateFlow("ALL")

    val state: StateFlow<FinanceUiState> = combine(
        repo.observeAll(),
        typeFilter
    ) { all, t ->
        val filtered = if (t == "ALL") all else all.filter { it.type == t }
        val totalIn = all.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalOut = all.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        FinanceUiState(
            totalIncome = totalIn,
            totalExpense = totalOut,
            balance = totalIn - totalOut,
            entries = filtered,
            typeFilter = t,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceUiState())

    fun setType(t: String) { typeFilter.value = t }
}