package com.mahallu.manager.feature.certificates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.CertificateEntity
import com.mahallu.manager.core.database.repository.CertificateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CertificateListViewModel @Inject constructor(
    certificateRepo: CertificateRepository
) : ViewModel() {
    val certificates: StateFlow<List<CertificateEntity>> = certificateRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
