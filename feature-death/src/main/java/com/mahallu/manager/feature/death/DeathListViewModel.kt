package com.mahallu.manager.feature.death

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.DeathEntity
import com.mahallu.manager.core.database.repository.DeathRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DeathListState(
    val items: List<DeathEntity> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class DeathListViewModel @Inject constructor(
    private val repo: DeathRepository
) : ViewModel() {
    private val query = MutableStateFlow("")

    val state: StateFlow<DeathListState> = combine(repo.observeAll(), query) { items, q ->
        val filtered = items.filter { d ->
            q.isBlank() || d.name.contains(q, true) || (d.fatherName?.contains(q, true) == true)
        }
        DeathListState(filtered, q, false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DeathListState())

    fun setQuery(q: String) { query.value = q }
}