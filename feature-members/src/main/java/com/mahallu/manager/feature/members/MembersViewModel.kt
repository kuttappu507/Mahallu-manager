package com.mahallu.manager.feature.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MembersUiState(
    val members: List<MemberEntity> = emptyList(),
    val totalCount: Int = 0,
    val query: String = "",
    val genderFilter: String = "ALL",
    val isLoading: Boolean = true
)

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val memberRepo: MemberRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val genderFilter = MutableStateFlow("ALL")

    val state: StateFlow<MembersUiState> = combine(
        memberRepo.observeAll(),
        query,
        genderFilter
    ) { members, q, filter ->
        val filtered = members.filter { m ->
            (filter == "ALL" || m.gender == filter) &&
            (q.isBlank() ||
                m.name.contains(q, ignoreCase = true) ||
                m.memberNumber.contains(q, ignoreCase = true) ||
                (m.mobile?.contains(q, ignoreCase = true) == true))
        }
        MembersUiState(
            members = filtered,
            totalCount = members.size,
            query = q,
            genderFilter = filter,
            isLoading = false
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MembersUiState())

    fun setQuery(q: String) { query.value = q }
    fun setGender(g: String) { genderFilter.value = g }
}
