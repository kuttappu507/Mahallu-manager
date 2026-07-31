package com.mahallu.manager.feature.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.ui.util.PdfShare
import java.io.File

@Composable
fun CollectionEntryScreen(
    onDone: () -> Unit,
    viewModel: CollectionEntryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    // Don't auto-navigate if a PDF was generated — let the user view/share it first.
    LaunchedEffect(state.saved, state.pdfPath) {
        if (state.saved && state.pdfPath == null) onDone()
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = "Collection Entry",
                showBack = true,
                onBackClick = onDone,
                trailingActions = {
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Rounded.Check, contentDescription = "Save", tint = colors.primaryIndigo)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                AppTextField(
                    value = state.receiptNumber,
                    onValueChange = { v -> viewModel.update { it.copy(receiptNumber = v) } },
                    label = "Receipt Number",
                    readOnly = true
                )
                Spacer(Modifier.height(16.dp))
                Text("Select Family", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                FamilySelector(state.families, state.selectedFamilyId, onSelect = { viewModel.selectFamily(it) })

                if (state.members.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Select Member", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    Spacer(Modifier.height(6.dp))
                    MemberSelector(state.members, state.selectedMemberId, onSelect = { viewModel.selectMember(it) })
                }

                Spacer(Modifier.height(16.dp))
                Text("Collection Type", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MONTHLY", "QUARTERLY", "YEARLY", "SPECIAL").forEach { t ->
                        ChipPill(text = t, selected = state.type == t, onClick = { viewModel.update { it.copy(type = t) } })
                    }
                }

                Spacer(Modifier.height(16.dp))
                AppTextField(
                    value = state.amount,
                    onValueChange = { v -> viewModel.update { it.copy(amount = v) } },
                    label = "Amount",
                    placeholder = "0.00",
                    keyboardType = KeyboardType.Decimal
                )

                Spacer(Modifier.height(16.dp))
                AppTextField(
                    value = Formatters.date(state.date),
                    onValueChange = { },
                    label = "Payment Date",
                    readOnly = true
                )

                Spacer(Modifier.height(16.dp))
                Text("Payment Method", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CASH", "UPI", "BANK", "CHEQUE", "OTHER").forEach { p ->
                        ChipPill(text = p, selected = state.paymentMethod == p, onClick = { viewModel.update { it.copy(paymentMethod = p) } })
                    }
                }

                Spacer(Modifier.height(16.dp))
                AppTextField(
                    value = state.remarks,
                    onValueChange = { v -> viewModel.update { it.copy(remarks = v) } },
                    label = "Remarks",
                    placeholder = "Optional",
                    maxLines = 3,
                    singleLine = false
                )

                if (!state.error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.error ?: "", color = colors.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(20.dp))

                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = Formatters.currency(state.amount.toDoubleOrNull() ?: 0.0),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.primaryIndigo,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                if (state.pdfPath != null) {
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = colors.success.copy(alpha = 0.10f),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        Text(
                            text = "✓ Saved. Receipt generated: ${File(state.pdfPath).name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AppButton(
                            text = "View Receipt",
                            onClick = { PdfShare.open(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.PictureAsPdf
                        )
                        AppButton(
                            text = "Share",
                            onClick = { PdfShare.share(context, File(state.pdfPath!!)) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Rounded.Share
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    AppButton(
                        text = "Done",
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AppButton(
                        text = if (state.isSaving) "Saving..." else "Save & Print Receipt",
                        onClick = { viewModel.save() },
                        isLoading = state.isSaving
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FamilySelector(
    families: List<FamilyEntity>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    val colors = LocalMahalluColors.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(families) { f ->
            val selected = f.id == selectedId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) colors.primaryIndigo else colors.surfaceVariant)
                    .clickable { onSelect(f.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${f.familyNumber} • ${f.houseName}",
                    color = if (selected) Color.White else colors.textPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun MemberSelector(
    members: List<MemberEntity>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    val colors = LocalMahalluColors.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(members) { m ->
            val selected = m.id == selectedId
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) colors.primaryIndigo else colors.surfaceVariant)
                    .clickable { onSelect(m.id) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = m.name,
                    color = if (selected) Color.White else colors.textPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}