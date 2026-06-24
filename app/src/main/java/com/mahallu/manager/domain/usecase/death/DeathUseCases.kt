package com.mahallu.manager.domain.usecase.death

import com.mahallu.manager.domain.model.Death
import com.mahallu.manager.domain.repository.DeathRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDeathsUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    operator fun invoke(): Flow<List<Death>> {
        return repository.getAllDeaths()
    }
}

class GetDeathByIdUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    suspend operator fun invoke(id: Int): Death? {
        return repository.getDeathById(id)
    }
}

class SaveDeathUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    suspend operator fun invoke(death: Death) {
        if (death.id == 0) {
            repository.insertDeath(death)
        } else {
            repository.updateDeath(death)
        }
    }
}

class DeleteDeathUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    suspend operator fun invoke(death: Death) {
        repository.deleteDeath(death)
    }
}

class SearchDeathsUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    operator fun invoke(query: String): Flow<List<Death>> {
        return repository.searchDeaths(query)
    }
}

class GetDeathsByDateRangeUseCase @Inject constructor(
    private val repository: DeathRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<Death>> {
        return repository.getDeathsByDateRange(startDate, endDate)
    }
}
