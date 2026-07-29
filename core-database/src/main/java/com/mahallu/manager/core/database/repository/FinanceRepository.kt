package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    private val dao: FinanceDao
) {
    fun observeAll(): Flow<List<FinanceEntryEntity>> = dao.observeAll()
    fun observeByType(type: String): Flow<List<FinanceEntryEntity>> = dao.observeByType(type)
    fun observeByRange(start: Long, end: Long): Flow<List<FinanceEntryEntity>> = dao.observeByRange(start, end)
    fun observeTotalIncome(start: Long, end: Long): Flow<Double?> = dao.observeTotalIncome(start, end)
    fun observeTotalExpense(start: Long, end: Long): Flow<Double?> = dao.observeTotalExpense(start, end)

    suspend fun getById(id: String): FinanceEntryEntity? = dao.getById(id)
    suspend fun all(): List<FinanceEntryEntity> = dao.all()

    suspend fun save(entity: FinanceEntryEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<FinanceEntryEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
}