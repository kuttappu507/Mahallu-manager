package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.Family
import com.mahallu.manager.domain.repository.FamilyRepository
import javax.inject.Inject

class SaveFamilyUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    suspend operator fun invoke(family: Family) {
        if (family.id == 0) {
            familyRepository.insertFamily(family)
        } else {
            familyRepository.updateFamily(family)
        }
    }
}
