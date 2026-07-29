package com.mahallu.manager.feature.welfare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.WelfareEntity
import com.mahallu.manager.core.database.repository.WelfareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelfareListState(
    val items: List<WelfareEntity> = emptyList(),
    val statusFilter: String = "ALL",
    val totalDisbursed: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class WelfareListViewModel @Inject constructor(
    private val repo: WelfareRepository
) : ViewModel() {
    private val statusFilter = MutableStateFlow("ALL")

    val state: StateFlow<WelfareListState> = combine(
        repo.observeAll(),
        statusFilter
    ) { all, s ->
        val filtered = if (s == "ALL") all else all.filter { it.status == s }
        val total = all.filter { it.status == "DISBURSED" }.sumOf { it.amount }
        WelfareListState(filtered, s, total, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WelfareListState())

    fun setStatus(s: String) { statusFilter.value = s }
    fun approve(id: String, approver: String) { viewModelScope.launch { repo.approve(id, approver) } }
    fun disburse(id: String) { viewModelScope.launch { repo.disburse(id) } }
    fun reject(id: String, approver: String, remarks: String?) { viewModelScope.launch { repo.reject(id, approver, remarks) } }
}