package com.mahallu.manager.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.repository.GlobalSearchRepository
import com.mahallu.manager.core.database.repository.GlobalSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: GlobalSearchResult = GlobalSearchResult(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val repo: GlobalSearchRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun setQuery(q: String) {
        _state.update { it.copy(query = q) }
        searchJob?.cancel()
        if (q.isBlank()) {
            _state.update { SearchUiState() }
            return
        }
        searchJob = viewModelScope.launch {
            delay(250) // debounce
            _state.update { it.copy(isLoading = true) }
            val results = repo.search(q)
            _state.update { it.copy(results = results, isLoading = false, hasSearched = true) }
        }
    }
}