package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DeathDao
import com.mahallu.manager.core.database.entity.DeathEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeathRepository @Inject constructor(private val dao: DeathDao) {
    fun observeAll(): Flow<List<DeathEntity>> = dao.observeAll()
    suspend fun getById(id: String): DeathEntity? = dao.getById(id)
    suspend fun search(q: String): List<DeathEntity> = dao.search(q)
    suspend fun all(): List<DeathEntity> = dao.all()
    suspend fun save(entity: DeathEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<DeathEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
}