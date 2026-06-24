package com.mahallu.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.domain.usecase.GetAllFamiliesUseCase
import com.mahallu.manager.domain.usecase.GetAllMembersUseCase
import com.mahallu.manager.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getAllFamiliesUseCase: GetAllFamiliesUseCase,
    getAllMembersUseCase: GetAllMembersUseCase,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                getAllFamiliesUseCase(),
                getAllMembersUseCase(),
                getDashboardStatsUseCase()
            ) { families, members, stats ->
                DashboardUiState.Success(
                    totalFamilies = families.size,
                    totalMembers = members.size,
                    monthlyCollection = stats.monthlyCollection,
                    pendingDues = stats.pendingDues,
                    totalDonations = stats.totalDonations,
                    welfareBeneficiaries = stats.welfareBeneficiaries
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val totalFamilies: Int = 0,
        val totalMembers: Int = 0,
        val monthlyCollection: Double = 0.0,
        val pendingDues: Double = 0.0,
        val totalDonations: Double = 0.0,
        val welfareBeneficiaries: Int = 0
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
