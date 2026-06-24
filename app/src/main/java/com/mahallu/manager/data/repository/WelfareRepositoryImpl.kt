package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.WelfareDao
import com.mahallu.manager.domain.model.WelfareRequest
import com.mahallu.manager.domain.repository.WelfareRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WelfareRepositoryImpl @Inject constructor(
    private val welfareDao: WelfareDao
) : WelfareRepository {

    override fun getAllWelfareRequests(): Flow<List<WelfareRequest>> {
        return welfareDao.getAllWelfareRequests().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWelfareRequestById(id: Int): Flow<WelfareRequest?> {
        return welfareDao.getWelfareRequestById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertWelfareRequest(request: WelfareRequest) {
        welfareDao.insertWelfareRequest(request.toEntity())
    }

    override suspend fun updateWelfareRequest(request: WelfareRequest) {
        welfareDao.updateWelfareRequest(request.toEntity())
    }

    override suspend fun deleteWelfareRequest(request: WelfareRequest) {
        welfareDao.deleteWelfareRequest(request.toEntity())
    }

    override fun getPendingRequests(): Flow<List<WelfareRequest>> {
        return welfareDao.getPendingRequests().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getApprovedRequests(): Flow<List<WelfareRequest>> {
        return welfareDao.getApprovedRequests().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
