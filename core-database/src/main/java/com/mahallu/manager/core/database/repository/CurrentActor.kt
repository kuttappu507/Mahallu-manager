package com.mahallu.manager.core.database.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AuditActor(
    val userId: String,
    val userName: String
)

@Singleton
class CurrentActor @Inject constructor() {
    private val _value = MutableStateFlow<AuditActor?>(null)
    val value: StateFlow<AuditActor?> = _value.asStateFlow()

    fun set(actor: AuditActor?) {
        _value.value = actor
    }

    fun snapshot(): AuditActor? = _value.value
}
