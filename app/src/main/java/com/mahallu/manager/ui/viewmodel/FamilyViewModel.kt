package com.mahallu.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.domain.model.Family
import com.mahallu.manager.domain.usecase.DeleteFamilyUseCase
import com.mahallu.manager.domain.usecase.GetAllFamiliesUseCase
import com.mahallu.manager.domain.usecase.SaveFamilyUseCase
import com.mahallu.manager.domain.usecase.SearchFamiliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val getAllFamiliesUseCase: GetAllFamiliesUseCase,
    private val searchFamiliesUseCase: SearchFamiliesUseCase,
    private val saveFamilyUseCase: SaveFamilyUseCase,
    private val deleteFamilyUseCase: DeleteFamilyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FamilyUiState>(FamilyUiState.Loading)
    val uiState: StateFlow<FamilyUiState> = _uiState

    init {
        loadFamilies()
    }

    fun loadFamilies() {
        viewModelScope.launch {
            _uiState.value = FamilyUiState.Loading
            getAllFamiliesUseCase().collect { families ->
                _uiState.value = FamilyUiState.Success(families)
            }
        }
    }

    fun searchFamilies(query: String) {
        viewModelScope.launch {
            searchFamiliesUseCase(query).collect { families ->
                _uiState.value = FamilyUiState.Success(families)
            }
        }
    }

    fun saveFamily(family: Family) {
        viewModelScope.launch {
            try {
                saveFamilyUseCase(family)
                loadFamilies()
            } catch (e: Exception) {
                _uiState.value = FamilyUiState.Error(e.message ?: "Failed to save family")
            }
        }
    }

    fun deleteFamily(family: Family) {
        viewModelScope.launch {
            try {
                deleteFamilyUseCase(family)
                loadFamilies()
            } catch (e: Exception) {
                _uiState.value = FamilyUiState.Error(e.message ?: "Failed to delete family")
            }
        }
    }
}

sealed class FamilyUiState {
    object Loading : FamilyUiState()
    data class Success(val families: List<Family>) : FamilyUiState()
    data class Error(val message: String) : FamilyUiState()
}
