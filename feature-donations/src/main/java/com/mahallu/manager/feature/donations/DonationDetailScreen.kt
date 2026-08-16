package com.mahallu.manager.feature.donations

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
import feature.donations.feature.donations.R
import java.io.File

@Composable
fun DonationDetailScreen(
    onBack: () -> Unit,
    viewModel: DonationDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = stringResource(R.string.donations_detail_title),
                    showBack = true,
                    onBackClick = onBack
                )
            }

            val donation = state.donation
            when {
                state.isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primaryIndigo)
                    }
                }
                donation == null -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                        Text(
                            text = state.error ?: stringResource(R.string.donations_error_not_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    item {
                        AppCard(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            backgroundColor = colors.accentCoral.copy(alpha = 0.08f),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Column {
                                Text(stringResource(R.string.donations_donor_name), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                                Text(
                                    text = donation.donorName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = Formatters.currency(donation.amount),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colors.accentCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        val mobileValue = donation.donorMobile?.takeIf { it.isNotBlank() }
                        val purposeValue = donation.purpose?.takeIf { it.isNotBlank() }
                        val remarksValue = donation.remarks?.takeIf { it.isNotBlank() }
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            items = listOfNotNull(
                                stringResource(R.string.donations_receipt_number) to donation.receiptNumber,
                                stringResource(R.string.donations_date) to Formatters.date(donation.date),
                                stringResource(R.string.donations_category) to stringResource(donationCategoryLabelRes(donation.category)),
                                stringResource(R.string.donations_payment_method) to stringResource(donationPaymentLabelRes(donation.paymentMethod)),
                                if (mobileValue != null) stringResource(R.string.donations_mobile) to mobileValue else null,
                                if (purposeValue != null) stringResource(R.string.donations_purpose) to purposeValue else null,
                                if (remarksValue != null) stringResource(R.string.donations_remarks) to remarksValue else null
                            )
                        )
                    }

                    item {
                        DetailSectionTitle(title = stringResource(R.string.donations_receipt_section))
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
                                    Text(stringResource(R.string.donations_pdf_generating), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                                }
                                pdfPath != null -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AppButton(
                                        text = stringResource(R.string.donations_view_receipt),
                                        onClick = { PdfShare.open(context, File(pdfPath)) },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = Icons.Rounded.PictureAsPdf
                                    )
                                    AppButton(
                                        text = stringResource(R.string.donations_share),
                                        onClick = { PdfShare.share(context, File(pdfPath)) },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = Icons.Rounded.Share
                                    )
                                }
                                else -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = state.error ?: stringResource(R.string.donations_pdf_failed),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = colors.error
                                        )
                                        AppButton(
                                            text = stringResource(R.string.donations_pdf_retry),
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
