package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.WelfareDao
import com.mahallu.manager.core.database.entity.WelfareEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelfareRepository @Inject constructor(
    private val dao: WelfareDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<WelfareEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<WelfareEntity?> = dao.observeById(id)
    fun observeByStatus(status: String): Flow<List<WelfareEntity>> = dao.observeByStatus(status)
    fun observeDisbursedBetween(start: Long, end: Long): Flow<Double?> = dao.observeDisbursedBetween(start, end)

    suspend fun getById(id: String): WelfareEntity? = dao.getById(id)

    suspend fun save(entity: WelfareEntity) {
        val existing = dao.getById(entity.id)
        dao.upsert(entity)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "WELFARE_REQUESTED" else "WELFARE_UPDATED",
            entityType = "welfare",
            entityId = entity.id,
            description = "${if (existing == null) "Created" else "Updated"} ${entity.category.lowercase()} request for ${entity.applicantName} (${entity.amount})"
        )
    }
    suspend fun saveAll(items: List<WelfareEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val item = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "WELFARE_DELETED",
            entityType = "welfare",
            entityId = id,
            description = "Deleted welfare request for ${item?.applicantName.orEmpty()}"
        )
    }

    suspend fun approve(id: String, approver: String) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "APPROVED", approvedBy = approver, approvedDate = System.currentTimeMillis()))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "WELFARE_APPROVED",
            entityType = "welfare",
            entityId = id,
            description = "Approved ${item.category.lowercase()} request for ${item.applicantName}"
        )
    }

    suspend fun reject(id: String, approver: String, remarks: String?) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "REJECTED", approvedBy = approver, remarks = remarks ?: item.remarks))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "WELFARE_REJECTED",
            entityType = "welfare",
            entityId = id,
            description = "Rejected ${item.category.lowercase()} request for ${item.applicantName}"
        )
    }

    suspend fun disburse(id: String) {
        val item = dao.getById(id) ?: return
        dao.upsert(item.copy(status = "DISBURSED", disbursedDate = System.currentTimeMillis()))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "WELFARE_DISBURSED",
            entityType = "welfare",
            entityId = id,
            description = "Disbursed ${item.amount} to ${item.applicantName}"
        )
    }
}