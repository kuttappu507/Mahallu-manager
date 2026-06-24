package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.repository.DonationRepository
import com.mahallu.manager.domain.repository.FinanceRepository
import com.mahallu.manager.domain.repository.SubscriptionRepository
import com.mahallu.manager.domain.repository.WelfareRepository
import javax.inject.Inject

data class DashboardStats(
    val monthlyCollection: Double = 0.0,
    val pendingDues: Double = 0.0,
    val totalDonations: Double = 0.0,
    val welfareBeneficiaries: Int = 0
)

class GetDashboardStatsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val donationRepository: DonationRepository,
    private val welfareRepository: WelfareRepository,
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(): DashboardStats {
        return DashboardStats(
            monthlyCollection = subscriptionRepository.getTotalCollection(),
            pendingDues = subscriptionRepository.getPendingDues(),
            totalDonations = donationRepository.getTotalDonations(),
            welfareBeneficiaries = 0
        )
    }
}
