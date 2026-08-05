package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.MarriageDao
import com.mahallu.manager.core.database.entity.MarriageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarriageRepository @Inject constructor(
    private val dao: MarriageDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<MarriageEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<MarriageEntity?> = dao.observeById(id)
    suspend fun getById(id: String): MarriageEntity? = dao.getById(id)
    suspend fun search(q: String): List<MarriageEntity> = dao.search(q)
    suspend fun all(): List<MarriageEntity> = dao.all()
    suspend fun save(entity: MarriageEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "MARRIAGE_RECORDED" else "MARRIAGE_UPDATED",
            entityType = "marriage",
            entityId = entity.id,
            description = "${if (existing == null) "Recorded" else "Updated"} marriage of ${entity.groomName} and ${entity.brideName}"
        )
    }
    suspend fun saveAll(items: List<MarriageEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val entity = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "MARRIAGE_DELETED",
            entityType = "marriage",
            entityId = id,
            description = "Deleted marriage of ${entity?.groomName.orEmpty()} and ${entity?.brideName.orEmpty()}"
        )
    }
}