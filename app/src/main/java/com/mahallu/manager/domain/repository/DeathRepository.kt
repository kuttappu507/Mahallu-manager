package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.DeathRecord
import kotlinx.coroutines.flow.Flow

interface DeathRepository {
    fun getAllDeaths(): Flow<List<DeathRecord>>
    fun getDeathById(id: Int): Flow<DeathRecord?>
    suspend fun insertDeath(death: DeathRecord)
    suspend fun updateDeath(death: DeathRecord)
    suspend fun deleteDeath(death: DeathRecord)
    fun searchDeaths(query: String): Flow<List<DeathRecord>>
}
