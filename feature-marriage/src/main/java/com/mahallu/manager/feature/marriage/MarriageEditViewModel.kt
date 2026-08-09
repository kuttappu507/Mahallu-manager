package com.mahallu.manager.feature.marriage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.MarriageEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.repository.MarriageRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.marriage.feature.marriage.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarriageEditState(
    val id: String = "",
    val registrationNumber: String = IdGenerator.marriageReg(),
    val brideId: String? = null,
    val brideName: String = "",
    val brideFatherName: String = "",
    val brideAge: String = "",
    val groomId: String? = null,
    val groomName: String = "",
    val groomFatherName: String = "",
    val groomAge: String = "",
    val witnessOneName: String = "",
    val witnessTwoName: String = "",
    val maharAmount: String = "",
    val nikahDate: Long = System.currentTimeMillis(),
    val registrationDate: Long = System.currentTimeMillis(),
    val nikahLocation: String = "",
    val remarks: String = "",
    val members: List<MemberEntity> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MarriageEditViewModel @Inject constructor(
    private val repo: MarriageRepository,
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MarriageEditState())
    val state: StateFlow<MarriageEditState> = _state.asStateFlow()

    /** Return the current marriage record (for caller to pre-fill the certificate). */
    fun current(): MarriageEditState = _state.value

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
                repo.observeById(id).collect { m ->
                    if (m == null || dirty) return@collect
                    _state.value = MarriageEditState(
                        id = m.id,
                        registrationNumber = m.registrationNumber,
                        brideId = m.brideId,
                        brideName = m.brideName,
                        brideFatherName = m.brideFatherName,
                        brideAge = m.brideAge?.toString().orEmpty(),
                        groomId = m.groomId,
                        groomName = m.groomName,
                        groomFatherName = m.groomFatherName,
                        groomAge = m.groomAge?.toString().orEmpty(),
                        witnessOneName = m.witnessOneName,
                        witnessTwoName = m.witnessTwoName,
                        maharAmount = m.maharAmount.toString(),
                        nikahDate = m.nikahDate,
                        registrationDate = m.registrationDate,
                        nikahLocation = m.nikahLocation.orEmpty(),
                        remarks = m.remarks.orEmpty(),
                        members = _state.value.members
                    )
                }
            }
        }
    }

    fun update(transform: (MarriageEditState) -> MarriageEditState) {
        dirty = true
        _state.update(transform)
    }

    fun selectBride(memberId: String) {
        val member = _state.value.members.firstOrNull { it.id == memberId } ?: return
        _state.update {
            it.copy(
                brideId = member.id,
                brideName = member.name,
                brideAge = Formatters.calculateAge(member.dateOfBirth).toString()
            )
        }
    }

    fun selectGroom(memberId: String) {
        val member = _state.value.members.firstOrNull { it.id == memberId } ?: return
        _state.update {
            it.copy(
                groomId = member.id,
                groomName = member.name,
                groomAge = Formatters.calculateAge(member.dateOfBirth).toString()
            )
        }
    }

    fun save() {
        val s = _state.value
        if (s.brideName.isBlank() || s.groomName.isBlank()) {
            _state.update { it.copy(error = context.getString(R.string.marriage_error_names_required)) }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = MarriageEntity(
                id = s.id.ifBlank { IdGenerator.newId() },
                registrationNumber = s.registrationNumber,
                brideId = s.brideId,
                brideName = s.brideName.trim(),
                brideFatherName = s.brideFatherName.trim(),
                brideAge = s.brideAge.toIntOrNull(),
                groomId = s.groomId,
                groomName = s.groomName.trim(),
                groomFatherName = s.groomFatherName.trim(),
                groomAge = s.groomAge.toIntOrNull(),
                witnessOneName = s.witnessOneName.trim(),
                witnessTwoName = s.witnessTwoName.trim(),
                maharAmount = s.maharAmount.toDoubleOrNull() ?: 0.0,
                nikahDate = s.nikahDate,
                registrationDate = s.registrationDate,
                nikahLocation = s.nikahLocation.trim().ifBlank { null },
                remarks = s.remarks.trim().ifBlank { null }
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}