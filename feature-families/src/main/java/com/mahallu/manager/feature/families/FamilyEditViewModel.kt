package com.mahallu.manager.feature.families

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FamilyEditState(
    val id: String = "",
    val familyNumber: String = "",
    val houseName: String = "",
    val houseNumber: String = "",
    val ward: String = "",
    val area: String = "",
    val address: String = "",
    val pincode: String = "",
    val primaryMobile: String = "",
    val secondaryMobile: String = "",
    val email: String = "",
    val notes: String = "",
    val status: String = "ACTIVE",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FamilyEditViewModel @Inject constructor(
    private val repo: FamilyRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(FamilyEditState())
    val state: StateFlow<FamilyEditState> = _state.asStateFlow()

    private var dirty = false

    init {
        val id = savedStateHandle.get<String>("familyId").orEmpty()
        if (id.isNotBlank()) loadExisting(id)
        else _state.update { it.copy(familyNumber = "FAM-${(1000..9999).random()}", isEditing = true) }
    }

    private fun loadExisting(id: String) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repo.observeById(id).collect { fam ->
                if (fam == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@collect
                }
                if (dirty) return@collect
                _state.value = FamilyEditState(
                    id = fam.id,
                    familyNumber = fam.familyNumber,
                    houseName = fam.houseName,
                    houseNumber = fam.houseNumber.orEmpty(),
                    ward = fam.ward.orEmpty(),
                    area = fam.area.orEmpty(),
                    address = fam.address,
                    pincode = fam.pincode.orEmpty(),
                    primaryMobile = fam.primaryMobile.orEmpty(),
                    secondaryMobile = fam.secondaryMobile.orEmpty(),
                    email = fam.email.orEmpty(),
                    notes = fam.notes.orEmpty(),
                    status = fam.status,
                    isEditing = true,
                    isLoading = false
                )
            }
        }
    }

    fun update(transform: (FamilyEditState) -> FamilyEditState) {
        dirty = true
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        if (s.houseName.isBlank() || s.address.isBlank()) {
            _state.update { it.copy(error = "House name and address are required") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val id = s.id.ifBlank { IdGenerator.newId() }
            val entity = FamilyEntity(
                id = id,
                familyNumber = s.familyNumber.ifBlank { "FAM-${id.takeLast(6).uppercase()}" },
                houseName = s.houseName.trim(),
                houseNumber = s.houseNumber.trim().ifBlank { null },
                ward = s.ward.trim().ifBlank { null },
                area = s.area.trim().ifBlank { null },
                address = s.address.trim(),
                pincode = s.pincode.trim().ifBlank { null },
                primaryMobile = s.primaryMobile.trim().ifBlank { null },
                secondaryMobile = s.secondaryMobile.trim().ifBlank { null },
                email = s.email.trim().ifBlank { null },
                notes = s.notes.trim().ifBlank { null },
                status = s.status,
                createdAt = now,
                updatedAt = now
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}