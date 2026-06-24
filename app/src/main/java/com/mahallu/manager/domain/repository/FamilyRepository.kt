package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.Family
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    fun getAllFamilies(): Flow<List<Family>>
    fun getFamilyById(id: Int): Flow<Family?>
    suspend fun insertFamily(family: Family)
    suspend fun updateFamily(family: Family)
    suspend fun deleteFamily(family: Family)
    suspend fun getFamilyByNumber(number: String): Family?
    fun searchFamilies(query: String): Flow<List<Family>>
}
