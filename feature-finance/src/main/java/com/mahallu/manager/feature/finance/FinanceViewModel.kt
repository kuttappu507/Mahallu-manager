package com.mahallu.manager.feature.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.repository.FinanceRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class FinanceUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val entries: List<FinanceEntryEntity> = emptyList(),
    val typeFilter: String = "ALL",
    val monthFilter: Long = -1L,
    val monthChips: List<Pair<String, Long>> = emptyList(),
    val isLoading: Boolean = true,
    val mahalluName: String = "",
    val monthLabel: String = "",
    val trendPct: Double? = null,
    val trendUp: Boolean = true
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repo: FinanceRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val typeFilter = MutableStateFlow("ALL")
    private val monthFilter = MutableStateFlow(DateUtils.startOfMonth())
    private val mahalluName = MutableStateFlow("")

    init {
        viewModelScope.launch {
            mahalluName.value = settingsRepo.getString("mahallu.name", "Mahallu Manager")
        }
    }

    private fun monthOptions(): List<Pair<String, Long>> {
        val now = Calendar.getInstance().timeInMillis
        val current = DateUtils.startOfMonth(now)
        val prev = DateUtils.startOfMonth(current - 1)
        return listOf(
            DateUtils.formatMonth(current) to current,
            DateUtils.formatMonth(prev) to prev
        )
    }

    val state: StateFlow<FinanceUiState> = combine(
        repo.observeAll(),
        typeFilter,
        monthFilter,
        mahalluName
    ) { all, t, m, name ->
        val inRange = if (m < 0L) all else all.filter { it.date in m..DateUtils.endOfMonth(m) }
        val filtered = if (t == "ALL") inRange else inRange.filter { it.type == t }
        val totalIn = inRange.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalOut = inRange.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val balance = totalIn - totalOut

        val prevStart = DateUtils.startOfMonth(m - 1)
        val prevEnd = DateUtils.endOfMonth(prevStart)
        val prevBalance = all
            .filter { it.date in prevStart..prevEnd }
            .sumOf { if (it.type == "INCOME") it.amount else -it.amount }
        val trendPct = if (prevBalance != 0.0) ((balance - prevBalance) / prevBalance) * 100.0 else null

        FinanceUiState(
            totalIncome = totalIn,
            totalExpense = totalOut,
            balance = balance,
            entries = filtered,
            typeFilter = t,
            monthFilter = m,
            monthChips = monthOptions(),
            isLoading = false,
            mahalluName = name,
            monthLabel = DateUtils.formatMonth(m),
            trendPct = trendPct,
            trendUp = trendPct?.let { it >= 0.0 } ?: true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceUiState())

    fun setType(t: String) { typeFilter.value = t }
    fun setMonth(m: Long) { monthFilter.value = m }
}