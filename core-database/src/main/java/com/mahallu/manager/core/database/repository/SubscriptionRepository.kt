package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.dao.SubscriptionDao
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.util.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val dao: SubscriptionDao,
    private val financeDao: FinanceDao,
    private val familyDao: FamilyDao,
    private val memberDao: MemberDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<SubscriptionEntity>> = dao.observeAll()
    fun observeByFamily(familyId: String): Flow<List<SubscriptionEntity>> = dao.observeByFamily(familyId)
    fun observeByRange(start: Long, end: Long): Flow<List<SubscriptionEntity>> = dao.observeByDateRange(start, end)
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?> = dao.observeTotalBetween(start, end)
    fun observeYears(): Flow<List<Int>> = dao.observeYears()

    suspend fun getById(id: String): SubscriptionEntity? = dao.getById(id)
    suspend fun recent(limit: Int): List<SubscriptionEntity> = dao.recent(limit)
    suspend fun all(): List<SubscriptionEntity> = dao.all()

    suspend fun save(entity: SubscriptionEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        financeDao.upsert(financeEntryFromSubscription(entity))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "SUBSCRIPTION_RECORDED" else "SUBSCRIPTION_UPDATED",
            entityType = "subscription",
            entityId = entity.id,
            description = "${if (existing == null) "Recorded" else "Updated"} ${entity.type.lowercase()} collection of ${entity.amount} (receipt ${entity.receiptNumber})"
        )
    }
    suspend fun saveAll(items: List<SubscriptionEntity>) {
        dao.upsertAll(items)
        financeDao.upsertAll(items.map { financeEntryFromSubscription(it) })
    }
    suspend fun delete(id: String) {
        val entity = dao.getById(id)
        dao.delete(id)
        financeDao.deleteByReceiptId(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "SUBSCRIPTION_DELETED",
            entityType = "subscription",
            entityId = id,
            description = "Deleted subscription receipt ${entity?.receiptNumber.orEmpty()}"
        )
    }

    /** Families that have NOT paid a monthly subscription for the current month. */
    suspend fun defaulters(year: Int, month: Int): List<DefaulterFamily> {
        val allFamilies = familyDao.page(limit = 1000, offset = 0)
        val paid = dao.recent(5000)
            .filter { it.type == "MONTHLY" && it.year == year && it.month == month }
            .map { it.familyId }
            .toSet()
        return allFamilies
            .filter { it.status == "ACTIVE" && it.id !in paid }
            .map { f ->
                val memberCount = memberDao.countByFamily(f.id)
                DefaulterFamily(
                    family = f,
                    memberCount = memberCount,
                    lastPaidDate = null,
                    monthsPending = 1
                )
            }
    }
}