package com.mahallu.manager.domain.usecase.marriage

import com.mahallu.manager.domain.model.Marriage
import com.mahallu.manager.domain.repository.MarriageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMarriagesUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    operator fun invoke(): Flow<List<Marriage>> {
        return repository.getAllMarriages()
    }
}

class GetMarriageByIdUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    suspend operator fun invoke(id: Int): Marriage? {
        return repository.getMarriageById(id)
    }
}

class SaveMarriageUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    suspend operator fun invoke(marriage: Marriage) {
        if (marriage.id == 0) {
            repository.insertMarriage(marriage)
        } else {
            repository.updateMarriage(marriage)
        }
    }
}

class DeleteMarriageUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    suspend operator fun invoke(marriage: Marriage) {
        repository.deleteMarriage(marriage)
    }
}

class SearchMarriagesUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    operator fun invoke(query: String): Flow<List<Marriage>> {
        return repository.searchMarriages(query)
    }
}

class GetMarriagesByDateRangeUseCase @Inject constructor(
    private val repository: MarriageRepository
) {
    operator fun invoke(startDate: Long, endDate: Long): Flow<List<Marriage>> {
        return repository.getMarriagesByDateRange(startDate, endDate)
    }
}
