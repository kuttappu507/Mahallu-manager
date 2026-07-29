package com.mahallu.manager.feature.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SubscriptionRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CollectionEntryState(
    val families: List<FamilyEntity> = emptyList(),
    val members: List<MemberEntity> = emptyList(),
    val selectedFamilyId: String = "",
    val selectedMemberId: String = "",
    val type: String = "MONTHLY",
    val amount: String = "500",
    val paymentMethod: String = "CASH",
    val remarks: String = "",
    val date: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val receiptNumber: String = "",
    val error: String? = null
)

@HiltViewModel
class CollectionEntryViewModel @Inject constructor(
    private val subRepo: SubscriptionRepository,
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionEntryState(receiptNumber = IdGenerator.receiptNumber()))
    val state: StateFlow<CollectionEntryState> = _state

    init {
        viewModelScope.launch {
            familyRepo.observeAll().collect { fams ->
                _state.update { it.copy(families = fams) }
            }
        }
        val preMemberId = savedStateHandle.get<String>("memberId").orEmpty()
        if (preMemberId.isNotBlank()) {
            viewModelScope.launch {
                val m = memberRepo.getById(preMemberId)
                if (m != null) {
                    _state.update {
                        it.copy(
                            selectedMemberId = m.id,
                            selectedFamilyId = m.familyId
                        )
                    }
                }
            }
        }
    }

    fun selectMember(memberId: String) {
        viewModelScope.launch {
            val m = memberRepo.getById(memberId)
            _state.update {
                it.copy(
                    selectedMemberId = m?.id.orEmpty(),
                    selectedFamilyId = m?.familyId.orEmpty()
                )
            }
        }
    }

    fun selectFamily(familyId: String) {
        viewModelScope.launch {
            val members = memberRepo.observeByFamily(familyId)
            members.collect { list ->
                _state.update {
                    it.copy(members = list, selectedFamilyId = familyId,
                        selectedMemberId = list.firstOrNull()?.id.orEmpty())
                }
                return@collect
            }
        }
    }

    fun update(transform: (CollectionEntryState) -> CollectionEntryState) {
        _state.update(transform)
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (s.selectedFamilyId.isBlank()) {
            _state.update { it.copy(error = "Select a family") }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Enter a valid amount") }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val cal = Calendar.getInstance().apply { timeInMillis = s.date }
            val entity = SubscriptionEntity(
                id = IdGenerator.newId(),
                receiptNumber = s.receiptNumber,
                familyId = s.selectedFamilyId,
                memberId = s.selectedMemberId.ifBlank { null },
                type = s.type,
                amount = amount,
                date = s.date,
                paymentMethod = s.paymentMethod,
                remarks = s.remarks.trim().ifBlank { null },
                receivedBy = null,
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1
            )
            subRepo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}