package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.entity.DeathEntity
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.entity.MarriageEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.entity.WelfareEntity

data class DashboardSummary(
    val totalFamilies: Int = 0,
    val totalMembers: Int = 0,
    val collectionThisMonth: Double = 0.0,
    val pendingDues: Double = 0.0,
    val donationsThisMonth: Double = 0.0,
    val welfareBeneficiaries: Int = 0,
    val recentActivities: List<ActivityItem> = emptyList()
)

data class ActivityItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: Long,
    val type: String
)

data class MonthlyTrendPoint(val label: String, val income: Double, val expense: Double)
data class CollectionTrendPoint(val label: String, val amount: Float)
data class DonationTrendPoint(val label: String, val amount: Float)

data class DefaulterFamily(
    val family: FamilyEntity,
    val memberCount: Int,
    val lastPaidDate: Long?,
    val monthsPending: Int
)

data class CashbookEntry(
    val date: Long,
    val description: String,
    val type: String,
    val category: String,
    val amount: Double,
    val paymentMethod: String,
    val reference: String?
)

data class GlobalSearchResult(
    val families: List<FamilyEntity> = emptyList(),
    val members: List<MemberEntity> = emptyList(),
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val donations: List<DonationEntity> = emptyList(),
    val marriages: List<MarriageEntity> = emptyList(),
    val deaths: List<DeathEntity> = emptyList(),
    val welfare: List<WelfareEntity> = emptyList(),
    val finance: List<FinanceEntryEntity> = emptyList()
) {
    val isEmpty: Boolean get() = families.isEmpty() && members.isEmpty() && subscriptions.isEmpty() &&
            donations.isEmpty() && marriages.isEmpty() && deaths.isEmpty() &&
            welfare.isEmpty() && finance.isEmpty()
    val total: Int get() = families.size + members.size + subscriptions.size + donations.size +
            marriages.size + deaths.size + welfare.size + finance.size
}