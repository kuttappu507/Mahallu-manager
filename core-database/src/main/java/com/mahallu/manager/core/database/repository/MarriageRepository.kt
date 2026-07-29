package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.MarriageDao
import com.mahallu.manager.core.database.entity.MarriageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarriageRepository @Inject constructor(private val dao: MarriageDao) {
    fun observeAll(): Flow<List<MarriageEntity>> = dao.observeAll()
    suspend fun getById(id: String): MarriageEntity? = dao.getById(id)
    suspend fun search(q: String): List<MarriageEntity> = dao.search(q)
    suspend fun all(): List<MarriageEntity> = dao.all()
    suspend fun save(entity: MarriageEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<MarriageEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
}