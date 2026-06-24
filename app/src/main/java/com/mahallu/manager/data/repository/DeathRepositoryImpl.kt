package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.DeathDao
import com.mahallu.manager.domain.model.DeathRecord
import com.mahallu.manager.domain.repository.DeathRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeathRepositoryImpl @Inject constructor(
    private val deathDao: DeathDao
) : DeathRepository {

    override fun getAllDeaths(): Flow<List<DeathRecord>> {
        return deathDao.getAllDeaths().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getDeathById(id: Int): Flow<DeathRecord?> {
        return deathDao.getDeathById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertDeath(death: DeathRecord) {
        deathDao.insertDeath(death.toEntity())
    }

    override suspend fun updateDeath(death: DeathRecord) {
        deathDao.updateDeath(death.toEntity())
    }

    override suspend fun deleteDeath(death: DeathRecord) {
        deathDao.deleteDeath(death.toEntity())
    }

    override fun searchDeaths(query: String): Flow<List<DeathRecord>> {
        return deathDao.searchDeaths("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
