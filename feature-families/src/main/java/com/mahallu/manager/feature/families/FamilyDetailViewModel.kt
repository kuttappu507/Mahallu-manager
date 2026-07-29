package com.mahallu.manager.feature.families

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FamilyDetailState(
    val family: FamilyEntity? = null,
    val members: List<MemberEntity> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FamilyDetailViewModel @Inject constructor(
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val familyIdFlow = MutableStateFlow(savedStateHandle.get<String>("familyId").orEmpty())

    val state: StateFlow<FamilyDetailState> = familyIdFlow.flatMapLatest { id ->
        if (id.isBlank()) flowOf(FamilyDetailState())
        else combine(familyRepo.observeById(id), memberRepo.observeByFamily(id)) { family, members ->
            FamilyDetailState(family = family, members = members, isLoading = false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FamilyDetailState())
}