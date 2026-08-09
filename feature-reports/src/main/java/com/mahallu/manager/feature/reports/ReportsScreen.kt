package com.mahallu.manager.feature.reports

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.PdfShare
import feature.reports.feature.reports.R
import java.io.File

private data class ReportType(val key: String, val title: String, val description: String, val icon: ImageVector)

@Composable
fun ReportsScreen(onBack: () -> Unit, viewModel: ReportsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current
    val reports = listOf(
        ReportType("FAMILY", stringResource(R.string.reports_family_register), stringResource(R.string.reports_family_register_desc), Icons.Rounded.Assessment),
        ReportType("MEMBER", stringResource(R.string.reports_member_register), stringResource(R.string.reports_member_register_desc), Icons.Rounded.Assessment),
        ReportType("COLLECTION", stringResource(R.string.reports_collection_report), stringResource(R.string.reports_collection_report_desc), Icons.Rounded.Description),
        ReportType("DONATION", stringResource(R.string.reports_donation_report), stringResource(R.string.reports_donation_report_desc), Icons.Rounded.Description),
        ReportType("FINANCE", stringResource(R.string.reports_finance_report), stringResource(R.string.reports_finance_report_desc), Icons.Rounded.AccountBalanceWallet),
        ReportType("MARRIAGE", stringResource(R.string.reports_marriage_register), stringResource(R.string.reports_marriage_register_desc), Icons.Rounded.Description),
        ReportType("DEATH", stringResource(R.string.reports_death_register), stringResource(R.string.reports_death_register_desc), Icons.Rounded.Description)
    )

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = stringResource(R.string.reports_title), showBack = true, onBackClick = onBack)
            if (!state.message.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .background(colors.successLight)
                        .padding(12.dp)
                ) {
                    Text(state.message ?: "", color = colors.success, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (state.lastGeneratedPath != null) {
                AppCard(
                    modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
                    backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.reports_last_report, File(state.lastGeneratedPath!!).name),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AppButton(
                                text = stringResource(R.string.reports_view),
                                onClick = { PdfShare.open(context, File(state.lastGeneratedPath!!)) },
                                leadingIcon = Icons.Rounded.PictureAsPdf,
                                modifier = Modifier.weight(1f)
                            )
                            AppButton(
                                text = stringResource(R.string.reports_share),
                                onClick = { PdfShare.share(context, File(state.lastGeneratedPath!!)) },
                                leadingIcon = Icons.Rounded.Share,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reports) { report ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.generate(report.key) },
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(report.icon, contentDescription = null, tint = colors.primaryIndigo)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    report.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    report.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                        }
                    }
                }
            }
        }
    }
}
