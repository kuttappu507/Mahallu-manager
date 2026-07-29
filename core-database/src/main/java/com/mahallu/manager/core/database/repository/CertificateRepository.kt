package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.CertificateDao
import com.mahallu.manager.core.database.entity.CertificateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CertificateRepository @Inject constructor(private val dao: CertificateDao) {
    fun observeAll(): Flow<List<CertificateEntity>> = dao.observeAll()
    fun observeByType(type: String): Flow<List<CertificateEntity>> = dao.observeByType(type)
    suspend fun getById(id: String): CertificateEntity? = dao.getById(id)
    suspend fun save(entity: CertificateEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<CertificateEntity>) = dao.upsertAll(items)
}