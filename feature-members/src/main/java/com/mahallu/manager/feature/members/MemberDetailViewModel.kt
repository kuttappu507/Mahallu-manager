package com.mahallu.manager.feature.members

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

data class MemberDetailState(
    val member: MemberEntity? = null,
    val family: FamilyEntity? = null,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MemberDetailViewModel @Inject constructor(
    private val memberRepo: MemberRepository,
    private val familyRepo: FamilyRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val memberIdFlow = MutableStateFlow(savedStateHandle.get<String>("memberId").orEmpty())

    val state: StateFlow<MemberDetailState> = memberIdFlow.flatMapLatest { id ->
        if (id.isBlank()) flowOf(MemberDetailState())
        else memberRepo.observeById(id).flatMapLatest { member ->
            if (member == null) flowOf(MemberDetailState())
            else combine(familyRepo.observeById(member.familyId), flowOf(member)) { family, m ->
                MemberDetailState(member = m, family = family, isLoading = false)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MemberDetailState())
}