package com.mahallu.manager.feature.finance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.repository.FinanceRepository
import com.mahallu.manager.core.util.IdGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.finance.feature.finance.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomeExpenseState(
    val type: String = "INCOME",
    val category: String = "OTHER_INCOME",
    val amount: String = "",
    val description: String = "",
    val paymentMethod: String = "CASH",
    val date: Long = System.currentTimeMillis(),
    val remarks: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IncomeExpenseEntryViewModel @Inject constructor(
    private val repo: FinanceRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(IncomeExpenseState())
    val state: StateFlow<IncomeExpenseState> = _state.asStateFlow()

    fun update(transform: (IncomeExpenseState) -> IncomeExpenseState) {
        _state.update(transform)
    }

    fun setType(type: String) {
        val cat = if (type == "INCOME") "OTHER_INCOME" else "OTHER_EXPENSE"
        _state.update { it.copy(type = type, category = cat) }
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (s.description.isBlank()) {
            _state.update { it.copy(error = context.getString(R.string.finance_error_description_required)) }
            return
        }
        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = context.getString(R.string.finance_error_valid_amount)) }
            return
        }
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val entity = FinanceEntryEntity(
                id = IdGenerator.newId(),
                type = s.type,
                category = s.category,
                amount = amount,
                date = s.date,
                description = s.description.trim(),
                paymentMethod = s.paymentMethod,
                reference = s.remarks.trim().ifBlank { null }
            )
            repo.save(entity)
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}