package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.entity.FamilyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepository @Inject constructor(
    private val dao: FamilyDao,
    private val memberDao: MemberDao
) {
    fun observeAll(): Flow<List<FamilyEntity>> = dao.observeAll()
    fun observeById(id: String): Flow<FamilyEntity?> = dao.observeById(id)
    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun getById(id: String): FamilyEntity? = dao.getById(id)
    suspend fun search(query: String): List<FamilyEntity> = dao.search(query)
    suspend fun count(): Int = dao.count()
    suspend fun memberCount(familyId: String): Int = memberDao.countByFamily(familyId)

    suspend fun save(family: FamilyEntity) = dao.upsert(family)
    suspend fun saveAll(items: List<FamilyEntity>) = dao.upsertAll(items)
    suspend fun delete(id: String) = dao.delete(id)
    suspend fun archive(id: String) {
        val family = dao.getById(id) ?: return
        dao.update(family.copy(status = "ARCHIVED", updatedAt = System.currentTimeMillis()))
    }

    suspend fun setStatus(id: String, status: String) {
        val family = dao.getById(id) ?: return
        dao.update(family.copy(status = status, updatedAt = System.currentTimeMillis()))
    }
}