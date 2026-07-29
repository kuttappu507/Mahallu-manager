package com.mahallu.manager.feature.marriage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.MarriageEntity
import com.mahallu.manager.core.database.repository.MarriageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarriageListState(
    val items: List<MarriageEntity> = emptyList(),
    val query: String = "",
    val yearFilter: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class MarriageListViewModel @Inject constructor(
    private val repo: MarriageRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val yearFilter = MutableStateFlow<Int?>(null)

    val state: StateFlow<MarriageListState> = combine(
        repo.observeAll(), query, yearFilter
    ) { items, q, y ->
        val filtered = items.filter { m ->
            (y == null || isSameYear(m.nikahDate, y)) &&
            (q.isBlank() ||
                m.brideName.contains(q, true) ||
                m.groomName.contains(q, true) ||
                m.registrationNumber.contains(q, true))
        }
        MarriageListState(filtered, q, y, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MarriageListState())

    fun setQuery(q: String) { query.value = q }
    fun setYear(y: Int?) { yearFilter.value = y }

    private fun isSameYear(ts: Long, year: Int): Boolean {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = ts }
        return cal.get(java.util.Calendar.YEAR) == year
    }
}