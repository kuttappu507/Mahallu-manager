package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.MarriageDao
import com.mahallu.manager.domain.model.MarriageRecord
import com.mahallu.manager.domain.repository.MarriageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarriageRepositoryImpl @Inject constructor(
    private val marriageDao: MarriageDao
) : MarriageRepository {

    override fun getAllMarriages(): Flow<List<MarriageRecord>> {
        return marriageDao.getAllMarriages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMarriageById(id: Int): Flow<MarriageRecord?> {
        return marriageDao.getMarriageById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertMarriage(marriage: MarriageRecord) {
        marriageDao.insertMarriage(marriage.toEntity())
    }

    override suspend fun updateMarriage(marriage: MarriageRecord) {
        marriageDao.updateMarriage(marriage.toEntity())
    }

    override suspend fun deleteMarriage(marriage: MarriageRecord) {
        marriageDao.deleteMarriage(marriage.toEntity())
    }

    override fun searchMarriages(query: String): Flow<List<MarriageRecord>> {
        return marriageDao.searchMarriages("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
