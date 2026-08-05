package com.mahallu.manager.feature.welfare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.WelfareEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.WelfareRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelfareEditState(
    val id: String = "",
    val familyId: String = "",
    val applicantName: String = "",
    val category: String = "MEDICAL",
    val amount: String = "",
    val reason: String = "",
    val status: String = "PENDING",
    val date: Long = System.currentTimeMillis(),
    val remarks: String = "",
    val families: List<FamilyEntity> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WelfareEditViewModel @Inject constructor(
    private val repo: WelfareRepository,
    private val familyRepo: FamilyRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(WelfareEditState())
    val state: StateFlow<WelfareEditState> = _state.asStateFlow()

    private var dirty = false

    init {
        viewModelScope.launch {
            familyRepo.observeAll().collect { fams ->
                _state.update { it.copy(families = fams) }
            }
        }
        val id = savedStateHandle.get<String>("id").orEmpty()
        if (id.isNotBlank()) {
            viewModelScope.launch {
                repo.observeById(id).collect { w ->
                    if (w == null || dirty) return@collect
                    _state.value = WelfareEditState(
                        id = w.id,
                        familyId = w.familyId,
                        applicantName = w.applicantName,
                        category = w.category,
                        amount = w.amount.toString(),
                        reason = w.reason,
                        status = w.status,
                        date = w.date,
                        remarks = w.remarks.orEmpty()
                    )
                }
            }
        }
    }

    fun update(transform: (WelfareEditState) -> WelfareEditState) {
        dirty = true
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (s.applicantName.isBlank() || s.familyId.isBlank()) {
            _state.update { it.copy(error = "Applicant and family required") }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Enter a valid amount") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = WelfareEntity(
                id = s.id.ifBlank { IdGenerator.newId() },
                familyId = s.familyId,
                applicantName = s.applicantName.trim(),
                category = s.category,
                amount = amount,
                reason = s.reason.trim(),
                status = s.status,
                date = s.date,
                remarks = s.remarks.trim().ifBlank { null }
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}