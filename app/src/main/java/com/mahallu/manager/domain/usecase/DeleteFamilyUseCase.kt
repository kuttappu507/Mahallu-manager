package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.Family
import com.mahallu.manager.domain.repository.FamilyRepository
import javax.inject.Inject

class DeleteFamilyUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    suspend operator fun invoke(family: Family) {
        familyRepository.deleteFamily(family)
    }
}
