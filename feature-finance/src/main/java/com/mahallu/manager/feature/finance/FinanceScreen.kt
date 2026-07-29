package com.mahallu.manager.feature.finance

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.FabAdd
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun FinanceScreen(
    onAddEntry: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = "Finance",
                showBack = false,
                onSearchClick = { },
                trailingActions = { FabAdd(onClick = onAddEntry) }
            )

            // Summary cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    label = "Total Income",
                    amount = state.totalIncome,
                    icon = Icons.Rounded.ArrowDownward,
                    color = colors.success,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Total Expense",
                    amount = state.totalExpense,
                    icon = Icons.Rounded.ArrowUpward,
                    color = colors.accentCoral,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))

            AppCard(
                modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
                backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.primaryIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Balance", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                        Text(
                            text = Formatters.currency(state.balance),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (state.balance >= 0) colors.success else colors.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("ALL", "INCOME", "EXPENSE")) { t ->
                    ChipPill(text = t, selected = state.typeFilter == t, onClick = { viewModel.setType(t) })
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.entries, key = { it.id }) { e -> FinanceRow(e) }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, amount: Double, icon: androidx.compose.ui.graphics.vector.ImageVector, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = LocalMahalluColors.current.textSecondary)
                Text(
                    text = Formatters.currencyShort(amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FinanceRow(e: FinanceEntryEntity) {
    val colors = LocalMahalluColors.current
    val isIncome = e.type == "INCOME"
    val accent = if (isIncome) colors.success else colors.accentCoral
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(e.description, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.Medium, maxLines = 1)
                Text(
                    text = "${e.category} • ${e.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(Formatters.date(e.date), style = MaterialTheme.typography.labelSmall, color = colors.textTertiary)
            }
            Text(
                text = "${if (isIncome) "+" else "-"}${Formatters.currency(e.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}