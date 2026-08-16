package com.mahallu.manager.feature.subscriptions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.repository.FamilyRepository
import com.mahallu.manager.core.database.repository.MemberRepository
import com.mahallu.manager.core.database.repository.SettingsRepository
import com.mahallu.manager.core.database.repository.SubscriptionRepository
import com.mahallu.manager.feature.certificates.pdf.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import feature.subscriptions.feature.subscriptions.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionDetailState(
    val subscription: SubscriptionEntity? = null,
    val familyName: String = "",
    val memberName: String = "",
    val isLoading: Boolean = true,
    val isGenerating: Boolean = false,
    val pdfPath: String? = null,
    val error: String? = null
)

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subRepo: SubscriptionRepository,
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    private val settingsRepo: SettingsRepository,
    private val pdfGenerator: PdfGenerator,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val collectionId = savedStateHandle.get<String>("collectionId").orEmpty()
    private val _state = MutableStateFlow(CollectionDetailState())
    val state: StateFlow<CollectionDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val sub = subRepo.getById(collectionId)
            if (sub == null) {
                _state.value = CollectionDetailState(isLoading = false, error = context.getString(R.string.collection_error_not_found))
                return@launch
            }
            val familyName = sub.familyId.takeIf { it.isNotBlank() }?.let { familyRepo.getById(it)?.houseName }.orEmpty()
            val memberName = sub.memberId?.takeIf { it.isNotBlank() }?.let { memberRepo.getById(it)?.name }.orEmpty()
            _state.value = CollectionDetailState(subscription = sub, familyName = familyName, memberName = memberName, isLoading = false)
            generateReceipt()
        }
    }

    fun generateReceipt() {
        val sub = _state.value.subscription ?: return
        val familyName = _state.value.familyName
        val memberName = _state.value.memberName
        _state.update { it.copy(isGenerating = true, error = null) }
        viewModelScope.launch {
            val path = generateSubscriptionReceipt(context, pdfGenerator, settingsRepo, sub, familyName, memberName)?.absolutePath
            _state.update {
                it.copy(
                    isGenerating = false,
                    pdfPath = path,
                    error = if (path == null) context.getString(R.string.collection_receipt_failed) else null
                )
            }
        }
    }
}
