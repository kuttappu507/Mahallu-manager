package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val dao: FinanceDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<FinanceEntryEntity>> = dao.observeAll()
    fun observeByType(type: String): Flow<List<FinanceEntryEntity>> = dao.observeByType(type)
    fun observeByRange(start: Long, end: Long): Flow<List<FinanceEntryEntity>> = dao.observeByRange(start, end)
    fun observeTotalIncome(start: Long, end: Long): Flow<Double?> = dao.observeTotalIncome(start, end)
    fun observeTotalExpense(start: Long, end: Long): Flow<Double?> = dao.observeTotalExpense(start, end)

    suspend fun getById(id: String): FinanceEntryEntity? = dao.getById(id)
    suspend fun all(): List<FinanceEntryEntity> = dao.all()

    suspend fun save(entity: FinanceEntryEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "FINANCE_ADDED" else "FINANCE_UPDATED",
            entityType = "finance",
            entityId = entity.id,
            description = "${if (existing == null) "Added" else "Updated"} ${entity.type.lowercase()} entry ${entity.category} for ${entity.amount}"
        )
    }
    suspend fun saveAll(items: List<FinanceEntryEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val entity = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "FINANCE_DELETED",
            entityType = "finance",
            entityId = id,
            description = "Deleted ${entity?.type?.lowercase().orEmpty()} entry ${entity?.category.orEmpty()}"
        )
    }
}