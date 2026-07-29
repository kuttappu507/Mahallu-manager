package com.mahallu.manager.feature.marriage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.MarriageEntity
import com.mahallu.manager.core.database.repository.MarriageRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarriageEditState(
    val id: String = "",
    val registrationNumber: String = IdGenerator.marriageReg(),
    val brideName: String = "",
    val brideFatherName: String = "",
    val brideAge: String = "",
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
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MarriageEditViewModel @Inject constructor(
    private val repo: MarriageRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MarriageEditState())
    val state: StateFlow<MarriageEditState> = _state.asStateFlow()

    init {
        val id = savedStateHandle.get<String>("id").orEmpty()
        if (id.isNotBlank()) {
            viewModelScope.launch {
                val m = repo.getById(id)
                if (m != null) {
                    _state.value = MarriageEditState(
                        id = m.id,
                        registrationNumber = m.registrationNumber,
                        brideName = m.brideName,
                        brideFatherName = m.brideFatherName,
                        brideAge = m.brideAge?.toString().orEmpty(),
                        groomName = m.groomName,
                        groomFatherName = m.groomFatherName,
                        groomAge = m.groomAge?.toString().orEmpty(),
                        witnessOneName = m.witnessOneName,
                        witnessTwoName = m.witnessTwoName,
                        maharAmount = m.maharAmount.toString(),
                        nikahDate = m.nikahDate,
                        registrationDate = m.registrationDate,
                        nikahLocation = m.nikahLocation.orEmpty(),
                        remarks = m.remarks.orEmpty()
                    )
                }
            }
        }
    }

    fun update(transform: (MarriageEditState) -> MarriageEditState) {
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        if (s.brideName.isBlank() || s.groomName.isBlank()) {
            _state.update { it.copy(error = "Bride and groom names required") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = MarriageEntity(
                id = s.id.ifBlank { IdGenerator.newId() },
                registrationNumber = s.registrationNumber,
                brideName = s.brideName.trim(),
                brideFatherName = s.brideFatherName.trim(),
                brideAge = s.brideAge.toIntOrNull(),
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