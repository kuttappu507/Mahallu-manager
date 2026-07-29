package com.mahallu.manager.feature.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SubscriptionsUiState(
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val totalThisMonth: Double = 0.0,
    val query: String = "",
    val typeFilter: String = "ALL",
    val isLoading: Boolean = true
)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repo: SubscriptionRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val typeFilter = MutableStateFlow("ALL")

    val state: StateFlow<SubscriptionsUiState> = combine(
        repo.observeAll(),
        query,
        typeFilter
    ) { all, q, t ->
        val filtered = all.filter { sub ->
            (t == "ALL" || sub.type == t) &&
            (q.isBlank() ||
                sub.receiptNumber.contains(q, true) ||
                (sub.remarks?.contains(q, true) == true))
        }
        val now = System.currentTimeMillis()
        val monthStart = com.mahallu.manager.core.util.DateUtils.startOfMonth(now)
        val monthEnd = com.mahallu.manager.core.util.DateUtils.endOfMonth(now)
        val total = all.filter { it.date in monthStart..monthEnd }.sumOf { it.amount }
        SubscriptionsUiState(
            subscriptions = filtered,
            totalThisMonth = total,
            query = q,
            typeFilter = t,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SubscriptionsUiState())

    fun setQuery(q: String) { query.value = q }
    fun setType(t: String) { typeFilter.value = t }
}