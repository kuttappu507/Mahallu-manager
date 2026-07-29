package com.mahallu.manager.feature.donations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.repository.DonationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DonationsUiState(
    val donations: List<DonationEntity> = emptyList(),
    val totalThisMonth: Double = 0.0,
    val query: String = "",
    val categoryFilter: String = "ALL",
    val isLoading: Boolean = true
)

@HiltViewModel
class DonationsViewModel @Inject constructor(
    private val repo: DonationRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val categoryFilter = MutableStateFlow("ALL")

    val state: StateFlow<DonationsUiState> = combine(
        repo.observeAll(),
        query,
        categoryFilter
    ) { all, q, c ->
        val filtered = all.filter { d ->
            (c == "ALL" || d.category == c) &&
            (q.isBlank() ||
                d.donorName.contains(q, true) ||
                d.receiptNumber.contains(q, true))
        }
        val now = System.currentTimeMillis()
        val monthStart = com.mahallu.manager.core.util.DateUtils.startOfMonth(now)
        val monthEnd = com.mahallu.manager.core.util.DateUtils.endOfMonth(now)
        val total = all.filter { it.date in monthStart..monthEnd }.sumOf { it.amount }
        DonationsUiState(
            donations = filtered,
            totalThisMonth = total,
            query = q,
            categoryFilter = c,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DonationsUiState())

    fun setQuery(q: String) { query.value = q }
    fun setCategory(c: String) { categoryFilter.value = c }
}