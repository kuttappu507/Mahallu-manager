package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.AuditLogDao
import com.mahallu.manager.core.database.entity.AuditLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditLogRepository @Inject constructor(private val dao: AuditLogDao) {
    fun observeRecent(limit: Int = 100): Flow<List<AuditLogEntity>> = dao.observeRecent(limit)
    suspend fun recent(limit: Int = 100): List<AuditLogEntity> = dao.recent(limit)

    suspend fun log(
        userId: String,
        userName: String,
        action: String,
        entityType: String? = null,
        entityId: String? = null,
        description: String? = null
    ) {
        dao.upsert(
            AuditLogEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                userName = userName,
                action = action,
                entityType = entityType,
                entityId = entityId,
                description = description,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}