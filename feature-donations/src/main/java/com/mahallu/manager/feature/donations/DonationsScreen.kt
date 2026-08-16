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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppSearchBar
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.FabAdd
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.donations.feature.donations.R

@Composable
fun DonationsScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onOpenItem: (String) -> Unit = {},
    viewModel: DonationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = stringResource(R.string.donations_title),
                showBack = true,
                onBackClick = onBack,
                trailingActions = { FabAdd(onClick = onAdd) }
            )

            AppCard(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                backgroundColor = colors.accentCoral.copy(alpha = 0.08f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Text(stringResource(R.string.donations_total_month), style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    Text(
                        text = Formatters.currency(state.totalThisMonth),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.accentCoral,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AppSearchBar(
                query = state.query,
                onQueryChange = viewModel::setQuery,
                placeholder = stringResource(R.string.donations_search_placeholder)
            )

            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("ALL", "GENERAL", "MASJID", "BUILDING", "EDUCATION", "MEDICAL", "WELFARE", "OTHER")) { c ->
                    ChipPill(text = if (c == "ALL") stringResource(R.string.donations_filter_all) else stringResource(donationCategoryLabelRes(c)), selected = state.categoryFilter == c, onClick = { viewModel.setCategory(c) })
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.donations, key = { it.id }) { d -> DonationRow(d, onClick = { onOpenItem(d.id) }) }
            }
        }
    }
}

@Composable
private fun DonationRow(d: DonationEntity, onClick: () -> Unit) {
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
                    .background(colors.accentCoral.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.VolunteerActivism, contentDescription = null, tint = colors.accentCoral)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = d.donorName,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.donations_row_detail, stringResource(donationCategoryLabelRes(d.category)), stringResource(donationPaymentLabelRes(d.paymentMethod))),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(
                    text = stringResource(R.string.donations_row_meta, Formatters.date(d.date), d.receiptNumber),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
            Text(
                text = Formatters.currency(d.amount),
                style = MaterialTheme.typography.titleMedium,
                color = colors.accentCoral,
                fontWeight = FontWeight.Bold
            )
        }
    }
}