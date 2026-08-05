package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.entity.MemberEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val dao: MemberDao,
    private val currentActor: CurrentActor,
    private val auditLog: AuditLogRepository
) {
    fun observeAll(): Flow<List<MemberEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<MemberEntity?> = dao.observeById(id)
    fun observeByFamily(familyId: String): Flow<List<MemberEntity>> = dao.observeByFamily(familyId)
    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun getById(id: String): MemberEntity? = dao.getById(id)
    suspend fun search(query: String): List<MemberEntity> = dao.search(query)
    suspend fun count(): Int = dao.count()

    suspend fun save(member: MemberEntity) {
        val existing = dao.getById(member.id)
        dao.upsert(member)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = if (existing == null) "MEMBER_ADDED" else "MEMBER_UPDATED",
            entityType = "member",
            entityId = member.id,
            description = "${if (existing == null) "Added" else "Updated"} member ${member.name}"
        )
    }
    suspend fun saveAll(items: List<MemberEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) {
        val member = dao.getById(id)
        dao.delete(id)
        auditLog.log(
            userId = currentActor.snapshot()?.userId.orEmpty(),
            userName = currentActor.snapshot()?.userName.orEmpty(),
            action = "MEMBER_DELETED",
            entityType = "member",
            entityId = id,
            description = "Deleted member ${member?.name.orEmpty()}"
        )
    }
}