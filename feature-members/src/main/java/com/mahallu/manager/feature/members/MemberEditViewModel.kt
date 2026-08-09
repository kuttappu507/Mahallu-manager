package com.mahallu.manager.feature.members

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.members.feature.members.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemberEditState(
    val id: String = "",
    val memberNumber: String = "",
    val familyId: String = "",
    val name: String = "",
    val gender: String = "MALE",
    val dateOfBirth: Long = System.currentTimeMillis(),
    val occupation: String = "",
    val education: String = "",
    val bloodGroup: String = "",
    val maritalStatus: String = "SINGLE",
    val mobile: String = "",
    val email: String = "",
    val nationality: String = "Indian",
    val address: String = "",
    val emergencyContactName: String = "",
    val emergencyContactNumber: String = "",
    val relationToHead: String = "",
    val notes: String = "",
    val families: List<FamilyEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MemberEditViewModel @Inject constructor(
    private val memberRepo: MemberRepository,
    private val familyRepo: FamilyRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MemberEditState())
    val state: StateFlow<MemberEditState> = _state.asStateFlow()

    private var dirty = false

    init {
        loadFamilies()
        val id = savedStateHandle.get<String>("memberId").orEmpty()
        val preFamilyId = savedStateHandle.get<String>("familyId").orEmpty()
        if (id.isNotBlank()) loadExisting(id)
        else _state.update {
            it.copy(
                memberNumber = "MEM-${(1000..9999).random()}",
                familyId = preFamilyId
            )
        }
    }

    private fun loadFamilies() {
        viewModelScope.launch {
            familyRepo.observeAll().collect { fams ->
                _state.update { it.copy(families = fams) }
            }
        }
    }

    private fun loadExisting(id: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            memberRepo.observeById(id).collect { m ->
                if (m == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@collect
                }
                if (dirty) return@collect
                _state.value = MemberEditState(
                    id = m.id,
                    memberNumber = m.memberNumber,
                    familyId = m.familyId,
                    name = m.name,
                    gender = m.gender,
                    dateOfBirth = m.dateOfBirth,
                    occupation = m.occupation.orEmpty(),
                    education = m.education.orEmpty(),
                    bloodGroup = m.bloodGroup.orEmpty(),
                    maritalStatus = m.maritalStatus ?: "SINGLE",
                    mobile = m.mobile.orEmpty(),
                    email = m.email.orEmpty(),
                    nationality = m.nationality.orEmpty(),
                    address = m.address.orEmpty(),
                    emergencyContactName = m.emergencyContactName.orEmpty(),
                    emergencyContactNumber = m.emergencyContactNumber.orEmpty(),
                    relationToHead = m.relationToHead.orEmpty(),
                    notes = m.notes.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun update(transform: (MemberEditState) -> MemberEditState) {
        dirty = true
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank() || s.familyId.isBlank()) {
            _state.update { it.copy(error = context.getString(R.string.member_edit_error_required)) }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = s.id.ifBlank { IdGenerator.newId() }
            val entity = MemberEntity(
                id = id,
                memberNumber = s.memberNumber.ifBlank { "MEM-${id.takeLast(6).uppercase()}" },
                familyId = s.familyId,
                name = s.name.trim(),
                gender = s.gender,
                dateOfBirth = s.dateOfBirth,
                occupation = s.occupation.trim().ifBlank { null },
                education = s.education.trim().ifBlank { null },
                bloodGroup = s.bloodGroup.trim().ifBlank { null },
                maritalStatus = s.maritalStatus.ifBlank { null },
                mobile = s.mobile.trim().ifBlank { null },
                email = s.email.trim().ifBlank { null },
                nationality = s.nationality.trim().ifBlank { null },
                address = s.address.trim().ifBlank { null },
                emergencyContactName = s.emergencyContactName.trim().ifBlank { null },
                emergencyContactNumber = s.emergencyContactNumber.trim().ifBlank { null },
                relationToHead = s.relationToHead.trim().ifBlank { null },
                notes = s.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now
            )
            memberRepo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}