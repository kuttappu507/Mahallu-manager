package com.mahallu.manager.feature.families

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamiliesUiState(
    val families: List<FamilyEntity> = emptyList(),
    val query: String = "",
    val statusFilter: String = "ALL",
    val isLoading: Boolean = true,
    val totalCount: Int = 0
)

@HiltViewModel
class FamiliesViewModel @Inject constructor(
    private val repo: FamilyRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val statusFilter = MutableStateFlow("ALL")

    val state: StateFlow<FamiliesUiState> = combine(
        repo.observeAll(),
        query,
        statusFilter
    ) { families, q, filter ->
        val filtered = families.filter { f ->
            (filter == "ALL" || f.status == filter) &&
            (q.isBlank() ||
                f.houseName.contains(q, ignoreCase = true) ||
                f.familyNumber.contains(q, ignoreCase = true) ||
                (f.primaryMobile?.contains(q, ignoreCase = true) == true) ||
                f.address.contains(q, ignoreCase = true))
        }
        FamiliesUiState(
            families = filtered,
            query = q,
            statusFilter = filter,
            isLoading = false,
            totalCount = families.size
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Eagerly, FamiliesUiState())

    fun setQuery(q: String) { query.value = q }
    fun setStatusFilter(filter: String) { statusFilter.value = filter }

    fun archive(id: String) {
        viewModelScope.launch { repo.archive(id) }
    }
}
