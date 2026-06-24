package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.Family
import com.mahallu.manager.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchFamiliesUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    operator fun invoke(query: String): Flow<List<Family>> {
        return familyRepository.searchFamilies(query)
    }
}
