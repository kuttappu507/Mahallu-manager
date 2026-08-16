package com.mahallu.manager.feature.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppSearchBar
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.FabAdd
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.ui.util.PdfShare
import feature.subscriptions.feature.subscriptions.R

@Composable
fun SubscriptionsScreen(
    onBack: () -> Unit,
    onAddCollection: () -> Unit,
    onOpenItem: (String) -> Unit = {},
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current
    val receiptFailedMessage = stringResource(R.string.collection_receipt_failed)
    val viewReceiptCd = stringResource(R.string.cd_view_receipt)

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = stringResource(R.string.subscriptions_title),
                showBack = true,
                onBackClick = onBack,
                trailingActions = { FabAdd(onClick = onAddCollection) }
            )

            // Total summary card
            AppCard(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Text(stringResource(R.string.subscriptions_total_this_month), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    Text(
                        text = Formatters.currency(state.totalThisMonth),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.primaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AppSearchBar(
                query = state.query,
                onQueryChange = viewModel::setQuery,
                placeholder = stringResource(R.string.subscriptions_search_placeholder)
            )

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "MONTHLY", "QUARTERLY", "YEARLY", "SPECIAL").forEach { t ->
                    ChipPill(text = t, selected = state.typeFilter == t, onClick = { viewModel.setType(t) })
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.subscriptions, key = { it.id }) { sub ->
                    SubscriptionRow(
                        sub,
                        onClick = { onOpenItem(sub.id) },
                        onViewReceipt = {
                            viewModel.generateReceipt(sub.id) { file ->
                                if (file != null) PdfShare.open(context, file)
                                else Toast.makeText(context, receiptFailedMessage, Toast.LENGTH_SHORT).show()
                            }
                        },
                        viewReceiptCd = viewReceiptCd
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionRow(
    sub: SubscriptionEntity,
    onClick: () -> Unit,
    onViewReceipt: () -> Unit,
    viewReceiptCd: String
) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.ReceiptLong, contentDescription = null, tint = colors.primaryIndigo)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sub.receiptNumber,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textTertiary
                )
                Text(
                    text = stringResource(R.string.subscriptions_row_detail, sub.type, sub.paymentMethod),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = Formatters.date(sub.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
            IconButton(onClick = onViewReceipt) {
                Icon(
                    imageVector = Icons.Rounded.PictureAsPdf,
                    contentDescription = viewReceiptCd,
                    tint = colors.primaryIndigo
                )
            }
            Text(
                text = Formatters.currency(sub.amount),
                style = MaterialTheme.typography.titleMedium,
                color = colors.primaryIndigo,
                fontWeight = FontWeight.Bold
            )
        }
    }
}