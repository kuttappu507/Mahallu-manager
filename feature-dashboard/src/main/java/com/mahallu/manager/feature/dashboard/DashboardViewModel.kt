package com.mahallu.manager.feature.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.repository.ActivityItem
import com.mahallu.manager.core.database.repository.CollectionTrendPoint
import com.mahallu.manager.core.database.repository.DashboardRepository
import com.mahallu.manager.core.database.repository.DashboardSummary
import com.mahallu.manager.core.database.repository.DonationTrendPoint
import com.mahallu.manager.core.database.repository.MonthlyTrendPoint
import com.mahallu.manager.core.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import feature.dashboard.feature.dashboard.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val summary: DashboardSummary = DashboardSummary(),
    val collectionTrend: List<CollectionTrendPoint> = emptyList(),
    val donationTrend: List<DonationTrendPoint> = emptyList(),
    val monthlyTrend: List<MonthlyTrendPoint> = emptyList(),
    val userName: String = "",
    val role: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val dashboardRepo: DashboardRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    val state: StateFlow<DashboardUiState> = combine(
        dashboardRepo.observeSummary(),
        dashboardRepo.observeCollectionTrend(),
        dashboardRepo.observeDonationTrend(),
        dashboardRepo.observeIncomeVsExpense()
    ) { summary, collectionTrend, donationTrend, monthlyTrend ->
        DashboardUiState(
            summary = summary,
            collectionTrend = collectionTrend,
            donationTrend = donationTrend,
            monthlyTrend = monthlyTrend,
            userName = sessionManager.getString(SessionManager.KEY_FULL_NAME, getApplication<Application>().getString(R.string.dashboard_user_fallback))
                ?: getApplication<Application>().getString(R.string.dashboard_user_fallback),
            role = sessionManager.getString(SessionManager.KEY_ROLE, "") ?: "",
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}