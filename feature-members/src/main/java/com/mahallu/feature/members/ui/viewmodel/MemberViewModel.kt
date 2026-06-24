package com.mahallu.feature.members.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.domain.model.Member
import com.mahallu.domain.model.MemberWithFamily
import com.mahallu.domain.usecase.member.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MemberUiState {
    object Loading : MemberUiState()
    data class Success(val members: List<MemberWithFamily>) : MemberUiState()
    data class Error(val message: String) : MemberUiState()
}

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val getAllMembersUseCase: GetAllMembersUseCase,
    private val searchMembersUseCase: SearchMembersUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val getMemberByIdUseCase: GetMemberByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MemberUiState>(MemberUiState.Loading)
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    init {
        loadAllMembers()
    }

    fun loadAllMembers() {
        viewModelScope.launch {
            _uiState.value = MemberUiState.Loading
            try {
                val members = getAllMembersUseCase()
                _uiState.value = MemberUiState.Success(members)
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchMembers(query: String) {
        viewModelScope.launch {
            _uiState.value = MemberUiState.Loading
            try {
                val members = if (query.isBlank()) {
                    getAllMembersUseCase()
                } else {
                    searchMembersUseCase(query)
                }
                _uiState.value = MemberUiState.Success(members)
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMember(member: Member) {
        viewModelScope.launch {
            try {
                addMemberUseCase(member)
                loadAllMembers()
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Failed to add member")
            }
        }
    }

    fun updateMember(member: Member) {
        viewModelScope.launch {
            try {
                updateMemberUseCase(member)
                loadAllMembers()
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Failed to update member")
            }
        }
    }

    fun deleteMember(memberId: Int) {
        viewModelScope.launch {
            try {
                deleteMemberUseCase(memberId)
                loadAllMembers()
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Failed to delete member")
            }
        }
    }

    fun getMemberById(memberId: Int) {
        viewModelScope.launch {
            try {
                val member = getMemberByIdUseCase(memberId)
            } catch (e: Exception) {
                _uiState.value = MemberUiState.Error(e.message ?: "Failed to get member")
            }
        }
    }
}
