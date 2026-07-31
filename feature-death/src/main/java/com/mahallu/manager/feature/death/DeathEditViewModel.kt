package com.mahallu.manager.feature.death

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DeathEntity
import com.mahallu.manager.core.database.repository.DeathRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeathEditState(
    val id: String = "",
    val registrationNumber: String = IdGenerator.deathReg(),
    val name: String = "",
    val fatherName: String = "",
    val age: String = "",
    val gender: String = "MALE",
    val dateOfDeath: Long = System.currentTimeMillis(),
    val burialDate: Long? = null,
    val burialLocation: String = "",
    val causeOfDeath: String = "",
    val remarks: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DeathEditViewModel @Inject constructor(
    private val repo: DeathRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(DeathEditState())
    val state: StateFlow<DeathEditState> = _state.asStateFlow()

    init {
        val id = savedStateHandle.get<String>("id").orEmpty()
        if (id.isNotBlank()) {
            viewModelScope.launch {
                repo.getById(id)?.let { d ->
                    _state.value = DeathEditState(
                        id = d.id,
                        registrationNumber = d.registrationNumber,
                        name = d.name,
                        fatherName = d.fatherName.orEmpty(),
                        age = d.age?.toString().orEmpty(),
                        gender = d.gender ?: "MALE",
                        dateOfDeath = d.dateOfDeath,
                        burialDate = d.burialDate,
                        burialLocation = d.burialLocation.orEmpty(),
                        causeOfDeath = d.causeOfDeath.orEmpty(),
                        remarks = d.remarks.orEmpty()
                    )
                }
            }
        }
    }

    /** Return the current death record (for caller to pre-fill the certificate). */
    fun current(): DeathEditState = _state.value

    fun update(transform: (DeathEditState) -> DeathEditState) {
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Name is required") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = DeathEntity(
                id = s.id.ifBlank { IdGenerator.newId() },
                registrationNumber = s.registrationNumber,
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