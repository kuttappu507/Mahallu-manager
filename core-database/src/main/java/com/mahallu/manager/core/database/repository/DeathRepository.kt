package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DeathDao
import com.mahallu.manager.core.database.entity.DeathEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeathRepository @Inject constructor(
    private val dao: DeathDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<DeathEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<DeathEntity?> = dao.observeById(id)
    suspend fun getById(id: String): DeathEntity? = dao.getById(id)
    suspend fun search(q: String): List<DeathEntity> = dao.search(q)
    suspend fun all(): List<DeathEntity> = dao.all()
    suspend fun save(entity: DeathEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "DEATH_RECORDED" else "DEATH_UPDATED",
            entityType = "death",
            entityId = entity.id,
            description = "${if (existing == null) "Recorded" else "Updated"} death of ${entity.name}"
        )
    }
    suspend fun saveAll(items: List<DeathEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val entity = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "DEATH_DELETED",
            entityType = "death",
            entityId = id,
            description = "Deleted death record of ${entity?.name.orEmpty()}"
        )
    }
}