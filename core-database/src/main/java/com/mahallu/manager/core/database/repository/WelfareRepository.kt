package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.WelfareDao
import com.mahallu.manager.core.database.entity.WelfareEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelfareRepository @Inject constructor(private val dao: WelfareDao) {
    fun observeAll(): Flow<List<WelfareEntity>> = dao.observeAll()
    fun observeByStatus(status: String): Flow<List<WelfareEntity>> = dao.observeByStatus(status)
    fun observeDisbursedBetween(start: Long, end: Long): Flow<Double?> = dao.observeDisbursedBetween(start, end)

    suspend fun getById(id: String): WelfareEntity? = dao.getById(id)

    suspend fun save(entity: WelfareEntity) = dao.upsert(entity)
    suspend fun saveAll(items: List<WelfareEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)

    suspend fun approve(id: String, approver: String) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "APPROVED", approvedBy = approver, approvedDate = System.currentTimeMillis()))
    }

    suspend fun reject(id: String, approver: String, remarks: String?) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "REJECTED", approvedBy = approver, remarks = remarks ?: item.remarks))
    }

    suspend fun disburse(id: String) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "DISBURSED", disbursedDate = System.currentTimeMillis()))
    }
}