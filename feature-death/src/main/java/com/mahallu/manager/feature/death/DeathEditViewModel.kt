package com.mahallu.manager.feature.death

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DeathEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.DeathRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.death.feature.death.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeathEditState(
    val id: String = "",
    val registrationNumber: String = IdGenerator.deathReg(),
    val memberId: String? = null,
    val familyId: String? = null,
    val name: String = "",
    val fatherName: String = "",
    val age: String = "",
    val gender: String = "MALE",
    val dateOfDeath: Long = System.currentTimeMillis(),
    val burialDate: Long? = null,
    val burialLocation: String = "",
    val causeOfDeath: String = "",
    val remarks: String = "",
    val members: List<MemberEntity> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DeathEditViewModel @Inject constructor(
    private val repo: DeathRepository,
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(DeathEditState())
    val state: StateFlow<DeathEditState> = _state.asStateFlow()

    private var dirty = false

    init {
        viewModelScope.launch {
            memberRepo.observeAll().collect { members ->
                _state.update { it.copy(members = members.filter { m -> m.isAlive }) }
            }
        }
        val id = savedStateHandle.get<String>("id").orEmpty()
        if (id.isNotBlank()) {
            viewModelScope.launch {
                repo.observeById(id).collect { d ->
                    if (d == null || dirty) return@collect
                    _state.value = DeathEditState(
                        id = d.id,
                        registrationNumber = d.registrationNumber,
                        memberId = d.memberId,
                        familyId = d.familyId,
                        name = d.name,
                        fatherName = d.fatherName.orEmpty(),
                        age = d.age?.toString().orEmpty(),
                        gender = d.gender ?: "MALE",
                        dateOfDeath = d.dateOfDeath,
                        burialDate = d.burialDate,
                        burialLocation = d.burialLocation.orEmpty(),
                        causeOfDeath = d.causeOfDeath.orEmpty(),
                        remarks = d.remarks.orEmpty(),
                        members = _state.value.members
                    )
                }
            }
        }
    }

    /** Return the current death record (for caller to pre-fill the certificate). */
    fun current(): DeathEditState = _state.value

    fun update(transform: (DeathEditState) -> DeathEditState) {
        dirty = true
        _state.update(transform)
    }

    fun selectDeceased(memberId: String) {
        val member = _state.value.members.firstOrNull { it.id == memberId } ?: return
        _state.update {
            it.copy(
                memberId = member.id,
                familyId = member.familyId,
                name = member.name,
                gender = member.gender,
                age = Formatters.calculateAge(member.dateOfBirth).toString()
            )
        }
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = context.getString(R.string.death_edit_error_name_required)) }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = DeathEntity(
                id = s.id.ifBlank { IdGenerator.newId() },
                registrationNumber = s.registrationNumber,
                memberId = s.memberId,
                familyId = s.familyId,
                name = s.name.trim(),
                fatherName = s.fatherName.trim().ifBlank { null },
                age = s.age.toIntOrNull(),
                gender = s.gender,
                dateOfDeath = s.dateOfDeath,
                burialDate = s.burialDate,
                burialLocation = s.burialLocation.trim().ifBlank { null },
                causeOfDeath = s.causeOfDeath.trim().ifBlank { null },
                remarks = s.remarks.trim().ifBlank { null }
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}