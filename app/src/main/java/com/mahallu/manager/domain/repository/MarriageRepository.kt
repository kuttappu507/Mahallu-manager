package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.MarriageRecord
import kotlinx.coroutines.flow.Flow

interface MarriageRepository {
    fun getAllMarriages(): Flow<List<MarriageRecord>>
    fun getMarriageById(id: Int): Flow<MarriageRecord?>
    suspend fun insertMarriage(marriage: MarriageRecord)
    suspend fun updateMarriage(marriage: MarriageRecord)
    suspend fun deleteMarriage(marriage: MarriageRecord)
    fun searchMarriages(query: String): Flow<List<MarriageRecord>>
}
