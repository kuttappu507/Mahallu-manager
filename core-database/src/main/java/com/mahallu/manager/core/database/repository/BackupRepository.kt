package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.BackupDao
import com.mahallu.manager.core.database.entity.BackupEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(private val dao: BackupDao) {
    fun observeAll(): Flow<List<BackupEntity>> = dao.observeAll()
    suspend fun latest(): BackupEntity? = dao.latest()
    suspend fun allSuccessful(): List<BackupEntity> = dao.allSuccessful()
    suspend fun save(entity: BackupEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<BackupEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
}