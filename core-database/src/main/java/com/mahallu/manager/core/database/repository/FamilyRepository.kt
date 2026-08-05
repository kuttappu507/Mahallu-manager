package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.AuditLogDao
import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.entity.FamilyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepository @Inject constructor(
    private val dao: FamilyDao,
    private val memberDao: MemberDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<FamilyEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<FamilyEntity?> = dao.observeById(id)
    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun getById(id: String): FamilyEntity? = dao.getById(id)
    suspend fun search(query: String): List<FamilyEntity> = dao.search(query)
    suspend fun count(): Int = dao.count()
    suspend fun memberCount(familyId: String): Int = memberDao.countByFamily(familyId)

    suspend fun save(family: FamilyEntity) {
        val existing = dao.getById(family.id)
        dao.upsert(family)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "FAMILY_ADDED" else "FAMILY_UPDATED",
            entityType = "family",
            entityId = family.id,
            description = "${if (existing == null) "Added" else "Updated"} family ${family.houseName}"
        )
    }
    suspend fun saveAll(items: List<FamilyEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val family = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "FAMILY_DELETED",
            entityType = "family",
            entityId = id,
            description = "Deleted family ${family?.houseName.orEmpty()}"
        )
    }
    suspend fun archive(id: String) {
        val family = dao.getById(id) ?: return
        dao.update(family.copy(status = "ARCHIVED", updatedAt = System.currentTimeMillis()))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "FAMILY_ARCHIVED",
            entityType = "family",
            entityId = id,
            description = "Archived family ${family.houseName}"
        )
    }

    suspend fun setStatus(id: String, status: String) {
        val family = dao.getById(id) ?: return
        dao.update(family.copy(status = status, updatedAt = System.currentTimeMillis()))
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "FAMILY_STATUS_UPDATED",
            entityType = "family",
            entityId = id,
            description = "Set family ${family.houseName} status to $status"
        )
    }
}