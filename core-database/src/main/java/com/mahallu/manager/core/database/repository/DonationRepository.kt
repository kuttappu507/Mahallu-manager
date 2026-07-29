package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.entity.DonationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationRepository @Inject constructor(
    private val dao: DonationDao
) {
    fun observeAll(): Flow<List<DonationEntity>> = dao.observeAll()
    fun observeByRange(start: Long, end: Long): Flow<List<DonationEntity>> = dao.observeByRange(start, end)
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?> = dao.observeTotalBetween(start, end)

    suspend fun getById(id: String): DonationEntity? = dao.getById(id)
    suspend fun recent(limit: Int): List<DonationEntity> = dao.recent(limit)
    suspend fun all(): List<DonationEntity> = dao.all()

    suspend fun save(entity: DonationEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<DonationEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
}