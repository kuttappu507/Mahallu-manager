package com.mahallu.manager.feature.subscriptions

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.DetailSectionTitle
import com.mahallu.manager.core.ui.components.InfoGridCard
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.ui.util.PdfShare
import feature.subscriptions.feature.subscriptions.R
import java.io.File

@Composable
fun CollectionDetailScreen(
    onBack: () -> Unit,
    viewModel: CollectionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = stringResource(R.string.subscriptions_detail_title),
                    showBack = true,
                    onBackClick = onBack
                )
            }

            val sub = state.subscription
            when {
                state.isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primaryIndigo)
                    }
                }
                sub == null -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                        Text(
                            text = state.error ?: stringResource(R.string.collection_error_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    val displayName = state.familyName.ifBlank { state.memberName }
                    item {
                        AppCard(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column {
                                Text(stringResource(R.string.collection_field_select_family), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                                Text(
                                    text = displayName.ifBlank { sub.receiptNumber },
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = Formatters.currency(sub.amount),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colors.primaryIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        val memberValue = state.memberName.takeIf { it.isNotBlank() }
                        val remarksValue = sub.remarks?.takeIf { it.isNotBlank() }
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            items = listOfNotNull(
                                stringResource(R.string.collection_field_receipt_number) to sub.receiptNumber,
                                stringResource(R.string.collection_field_payment_date) to Formatters.date(sub.date),
                                stringResource(R.string.collection_field_collection_type) to sub.type,
                                stringResource(R.string.collection_field_payment_method) to sub.paymentMethod,
                                if (memberValue != null) stringResource(R.string.collection_field_select_member) to memberValue else null,
                                if (remarksValue != null) stringResource(R.string.collection_field_remarks) to remarksValue else null
                            )
                        )
                    }

                    item {
                        DetailSectionTitle(title = stringResource(R.string.collection_receipt_section))
                    }

                    item {
                        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                            val pdfPath = state.pdfPath
                            when {
                                state.isGenerating -> Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(color = colors.primaryIndigo)
                                    Text(stringResource(R.string.collection_receipt_generating), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                                }
                                pdfPath != null -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AppButton(
                                        text = stringResource(R.string.collection_view_receipt),
                                        onClick = { PdfShare.open(context, File(pdfPath)) },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = Icons.Rounded.PictureAsPdf
                                    )
                                    AppButton(
                                        text = stringResource(R.string.collection_share),
                                        onClick = { PdfShare.share(context, File(pdfPath)) },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = Icons.Rounded.Share
                                    )
                                }
                                else -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = state.error ?: stringResource(R.string.collection_receipt_failed),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.error
                                        )
                                        AppButton(
                                            text = stringResource(R.string.collection_receipt_retry),
                                            onClick = { viewModel.generateReceipt() },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
