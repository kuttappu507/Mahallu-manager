package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.WelfareRequest
import kotlinx.coroutines.flow.Flow

interface WelfareRepository {
    fun getAllWelfareRequests(): Flow<List<WelfareRequest>>
    fun getWelfareRequestById(id: Int): Flow<WelfareRequest?>
    suspend fun insertWelfareRequest(request: WelfareRequest)
    suspend fun updateWelfareRequest(request: WelfareRequest)
    suspend fun deleteWelfareRequest(request: WelfareRequest)
    fun getPendingRequests(): Flow<List<WelfareRequest>>
    fun getApprovedRequests(): Flow<List<WelfareRequest>>
}
