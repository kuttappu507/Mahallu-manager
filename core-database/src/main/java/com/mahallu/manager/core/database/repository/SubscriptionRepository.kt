package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.FamilyDao
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
    private val familyDao: FamilyDao,
    private val memberDao: MemberDao
) {
    fun observeAll(): Flow<List<SubscriptionEntity>> = dao.observeAll()
    fun observeByFamily(familyId: String): Flow<List<SubscriptionEntity>> = dao.observeByFamily(familyId)
    fun observeByRange(start: Long, end: Long): Flow<List<SubscriptionEntity>> = dao.observeByDateRange(start, end)
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?> = dao.observeTotalBetween(start, end)
    fun observeYears(): Flow<List<Int>> = dao.observeYears()

    suspend fun getById(id: String): SubscriptionEntity? = dao.getById(id)
    suspend fun recent(limit: Int): List<SubscriptionEntity> = dao.recent(limit)
    suspend fun all(): List<SubscriptionEntity> = dao.all()

    suspend fun save(entity: SubscriptionEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<SubscriptionEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)

    /** Families that have NOT paid a monthly subscription for the current month. */
    suspend fun defaulters(year: Int, month: Int): List<DefaulterFamily> {
        val start = DateUtils.monthStartTimestamp(year, month)
        val end = start + 30L * 24 * 60 * 60 * 1000
        val families = familyDao.observeAll().let { emptyList<com.mahallu.manager.core.database.entity.FamilyEntity>() }
        // Compute by reading from DB
        val allFamilies = familyDao.page(limit = 1000, offset = 0)
        val paid = dao.recent(5000)
            .filter { it.type == "MONTHLY" && it.year == year && it.month == month }
            .map { it.familyId }
            .toSet()
        return allFamilies
            .filter { it.status == "ACTIVE" && it.id !in paid }
            .map { f ->
                val lastPaid = paid.firstOrNull() // simplified; would query separately
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