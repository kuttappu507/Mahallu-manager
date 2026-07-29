package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.NotificationDao
import com.mahallu.manager.core.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(private val dao: NotificationDao) {
    fun observeRecent(limit: Int = 50): Flow<List<NotificationEntity>> = dao.observeRecent(limit)
    fun observeUnreadCount(): Flow<Int> = dao.observeUnreadCount()

    suspend fun add(title: String, message: String, type: String = "INFO", actionRoute: String? = null) {
        dao.upsert(
            NotificationEntity(
                id = java.util.UUID.randomUUID().toString(),
                title = title,
                message = message,
                type = type,
                actionRoute = actionRoute
            )
        )
    }

    suspend fun markRead(id: String) = dao.markRead(id)
    suspend fun markAllRead() = dao.markAllRead()
}