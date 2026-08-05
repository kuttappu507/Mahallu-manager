package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.entity.DonationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationRepository @Inject constructor(
    private val dao: DonationDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<DonationEntity>> = dao.observeAll()
    fun observeByRange(start: Long, end: Long): Flow<List<DonationEntity>> = dao.observeByRange(start, end)
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?> = dao.observeTotalBetween(start, end)

    suspend fun getById(id: String): DonationEntity? = dao.getById(id)
    suspend fun recent(limit: Int): List<DonationEntity> = dao.recent(limit)
    suspend fun all(): List<DonationEntity> = dao.all()

    suspend fun save(entity: DonationEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "DONATION_ADDED" else "DONATION_UPDATED",
            entityType = "donation",
            entityId = entity.id,
            description = "${if (existing == null) "Recorded" else "Updated"} donation of ${entity.amount} from ${entity.donorName}"
        )
    }
    suspend fun saveAll(items: List<DonationEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val entity = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "DONATION_DELETED",
            entityType = "donation",
            entityId = id,
            description = "Deleted donation of ${entity?.amount ?: 0.0} from ${entity?.donorName.orEmpty()}"
        )
    }
}