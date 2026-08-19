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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SubscriptionsUiState(
    val subscriptions: List<SubscriptionEntity> = emptyList(),
    val totalThisMonth: Double = 0.0,
    val query: String = "",
    val typeFilter: String = "ALL",
    val isLoading: Boolean = true
)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repo: SubscriptionRepository,
    private val familyRepo: FamilyRepository,
    private val memberRepo: MemberRepository,
    @ApplicationContext private val context: Context,
    private val settingsRepo: SettingsRepository,
    private val pdfGenerator: PdfGenerator
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val typeFilter = MutableStateFlow("ALL")

    val state: StateFlow<SubscriptionsUiState> = combine(
        repo.observeAll(),
        query,
        typeFilter
    ) { all, q, t ->
        val filtered = all.filter { sub ->
            (t == "ALL" || sub.type == t) &&
            (q.isBlank() ||
                sub.receiptNumber.contains(q, true) ||
                (sub.remarks?.contains(q, true) == true))
        }
        val now = System.currentTimeMillis()
        val monthStart = com.mahallu.manager.core.util.DateUtils.startOfMonth(now)
        val monthEnd = com.mahallu.manager.core.util.DateUtils.endOfMonth(now)
        val total = all.filter { it.date in monthStart..monthEnd }.sumOf { it.amount }
        SubscriptionsUiState(
            subscriptions = filtered,
            totalThisMonth = total,
            query = q,
            typeFilter = t,
            isLoading = false
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Eagerly, SubscriptionsUiState())

    fun setQuery(q: String) { query.value = q }
    fun setType(t: String) { typeFilter.value = t }

    /**
     * Regenerates the receipt PDF for an existing collection so it can be
     * previewed directly from the list. [onResult] is called with the file
     * (null on failure).
     */
    fun generateReceipt(subscriptionId: String, onResult: (File?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val sub = repo.getById(subscriptionId)
            if (sub == null) {
                onResult(null)
                return@launch
            }
            val familyName = sub.familyId.takeIf { it.isNotBlank() }?.let { familyRepo.getById(it)?.houseName }.orEmpty()
            val memberName = sub.memberId?.takeIf { it.isNotBlank() }?.let { memberRepo.getById(it)?.name }.orEmpty()
            onResult(generateSubscriptionReceipt(context, pdfGenerator, settingsRepo, sub, familyName, memberName))
        }
    }
}