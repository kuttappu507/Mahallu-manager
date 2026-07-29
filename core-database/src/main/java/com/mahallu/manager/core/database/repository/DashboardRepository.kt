package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.AuditLogDao
import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.dao.SubscriptionDao
import com.mahallu.manager.core.database.dao.WelfareDao
import com.mahallu.manager.core.database.entity.AuditLogEntity
import com.mahallu.manager.core.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val familyDao: FamilyDao,
    private val memberDao: MemberDao,
    private val subscriptionDao: SubscriptionDao,
    private val donationDao: DonationDao,
    private val financeDao: FinanceDao,
    private val welfareDao: WelfareDao,
    private val auditDao: AuditLogDao
) {
    fun observeSummary(): Flow<DashboardSummary> {
        val now = System.currentTimeMillis()
        val monthStart = DateUtils.startOfMonth(now)
        val monthEnd = DateUtils.endOfMonth(now)

        val statsFlow = combine(
            familyDao.observeCount(),
            memberDao.observeCount(),
            subscriptionDao.observeTotalBetween(monthStart, monthEnd),
            donationDao.observeTotalBetween(monthStart, monthEnd)
        ) { families, members, collection, donations ->
            StatsData(families, members, collection ?: 0.0, donations ?: 0.0)
        }

        return combine(statsFlow, welfareDao.observeAll(), auditDao.observeRecent(8)) { stats, welfare, logs ->
            DashboardSummary(
                totalFamilies = stats.families,
                totalMembers = stats.members,
                collectionThisMonth = stats.collection,
                pendingDues = 0.0,
                donationsThisMonth = stats.donations,
                welfareBeneficiaries = welfare.count { it.status == "APPROVED" || it.status == "DISBURSED" },
                recentActivities = logs.map { it.toActivityItem() }
            )
        }
    }

    fun observeCollectionTrend(): Flow<List<CollectionTrendPoint>> {
        val months = buildMonthPairs(12)
        val flows = months.map { (_, start, end) -> subscriptionDao.observeTotalBetween(start, end) }
        return combine(flows) { values ->
            months.zip(values.toList()).map { (info, v) ->
                CollectionTrendPoint(info.first, (v ?: 0.0).toFloat())
            }
        }
    }

    fun observeIncomeVsExpense(): Flow<List<MonthlyTrendPoint>> {
        val months = buildMonthPairs(12)
        val incomeFlows = months.map { (_, start, end) -> financeDao.observeTotalIncome(start, end) }
        val expenseFlows = months.map { (_, start, end) -> financeDao.observeTotalExpense(start, end) }

        return combine(incomeFlows + expenseFlows) { values ->
            val incomes = values.slice(0 until months.size).map { it ?: 0.0 }
            val expenses = values.slice(months.size until values.size).map { it ?: 0.0 }
            months.zip(incomes.zip(expenses)).map { (info, pair) ->
                MonthlyTrendPoint(info.first, pair.first, pair.second)
            }
        }
    }

    fun observeDonationTrend(): Flow<List<DonationTrendPoint>> {
        val months = buildMonthPairs(6)
        val flows = months.map { (_, start, end) -> donationDao.observeTotalBetween(start, end) }
        return combine(flows) { values ->
            months.zip(values.toList()).map { (info, v) ->
                DonationTrendPoint(info.first, (v ?: 0.0).toFloat())
            }
        }
    }

    private data class StatsData(
        val families: Int,
        val members: Int,
        val collection: Double,
        val donations: Double
    )

    private fun buildMonthPairs(count: Int): List<Triple<String, Long, Long>> {
        val now = System.currentTimeMillis()
        return (0 until count).map { idx ->
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = now
            cal.add(java.util.Calendar.MONTH, -(count - 1 - idx))
            val y = cal.get(java.util.Calendar.YEAR)
            val m = cal.get(java.util.Calendar.MONTH)
            val start = DateUtils.monthStartTimestamp(y, m)
            val end = start + 31L * 24 * 60 * 60 * 1000
            Triple(DateUtils.monthLabel(y, m), start, end)
        }
    }

    private fun AuditLogEntity.toActivityItem(): ActivityItem {
        val subtitle = listOfNotNull(entityType, description).joinToString(" • ")
        return ActivityItem(
            id = id,
            title = action.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            subtitle = subtitle,
            timestamp = timestamp,
            type = entityType ?: "system"
        )
    }
}