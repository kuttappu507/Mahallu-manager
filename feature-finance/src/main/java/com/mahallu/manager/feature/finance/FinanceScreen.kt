package com.mahallu.manager.feature.finance

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
import com.mahallu.manager.core.ui.theme.Rose
import com.mahallu.manager.core.ui.util.Formatters
import feature.finance.feature.finance.R
import kotlinx.coroutines.delay

@Composable
fun FinanceScreen(
    onAddEntry: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.finance_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = state.mahalluName.ifBlank { stringResource(R.string.finance_title) },
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                Spacer(Modifier.weight(1f))
                IconCircleButton(
                    icon = Icons.Rounded.IosShare,
                    onClick = { shareFinance(context, state) },
                    backgroundColor = Color.White,
                    tint = colors.textPrimary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    BalanceCard(balance = state.balance, income = state.totalIncome, expense = state.totalExpense)
                }

                item {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.monthChips.forEach { (label, startMillis) ->
                            ChipPill(text = label, selected = state.monthFilter == startMillis, onClick = { viewModel.setMonth(startMillis) })
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(14.dp))
                    val typeTabs = listOf(
                        "ALL" to stringResource(R.string.finance_filter_all),
                        "INCOME" to stringResource(R.string.finance_filter_income),
                        "EXPENSE" to stringResource(R.string.finance_filter_expense)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(typeTabs) { (key, label) ->
                            ChipPill(text = label, selected = state.typeFilter == key, onClick = { viewModel.setType(key) })
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SumTile(
                            label = stringResource(R.string.finance_total_income),
                            amount = state.totalIncome,
                            icon = Icons.Rounded.ArrowUpward,
                            accent = colors.successDark,
                            tintBackground = colors.successTint,
                            modifier = Modifier.weight(1f)
                        )
                        SumTile(
                            label = stringResource(R.string.finance_total_expenses),
                            amount = state.totalExpense,
                            icon = Icons.Rounded.ArrowDownward,
                            accent = colors.rose,
                            tintBackground = colors.roseTint,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.finance_recent_activity),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    Text(
                        text = stringResource(R.string.finance_transactions_this_month, state.entries.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }

                items(state.entries, key = { it.id }) { e ->
                    FinanceRow(e, modifier = Modifier.padding(top = 9.dp))
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddEntry,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp),
            containerColor = colors.primaryIndigo,
            contentColor = Color.White,
            icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.finance_add_entry), fontWeight = FontWeight.Bold, maxLines = 1) }
        )
    }
}

@Composable
private fun SumTile(
    label: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    tintBackground: Color,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(tintBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp))
            }
            Text(
                text = Formatters.currencyShort(amount),
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = LocalMahalluColors.current.textSecondary
            )
        }
    }
}

@Composable
private fun BalanceCard(balance: Double, income: Double, expense: Double) {
    val colors = LocalMahalluColors.current
    val shape = RoundedCornerShape(26.dp)
    val isPositive = balance >= 0
    val gradient = if (isPositive) {
        listOf(PrimaryIndigo, colors.primaryDark)
    } else {
        listOf(colors.error, Color(0xFFB91C1C))
    }
    val sheen = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(350)
        sheen.animateTo(1f, tween(1100, easing = LinearEasing))
    }
    val sheenValue = sheen.value

    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .shadow(18.dp, shape, ambientColor = PrimaryIndigo.copy(alpha = 0.4f), spotColor = PrimaryIndigo.copy(alpha = 0.3f))
            .clip(shape)
            .background(Brush.linearGradient(gradient))
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        // Sheen sweep
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val band = w * 0.55f
            val startX = -band + sheenValue * (w + band * 2f)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.16f), Color.Transparent),
                    start = Offset(startX, 0f),
                    end = Offset(startX + band, size.height)
                )
            )
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.finance_net_balance_month, currentMonthLabel()),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color.White)
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                            contentDescription = null,
                            tint = if (isPositive) colors.successDark else colors.rose,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isPositive) stringResource(R.string.finance_trend_up) else stringResource(R.string.finance_trend_down),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPositive) colors.successDark else colors.rose,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = Formatters.currency(balance),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.22f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1f
                        )
                    }
                    .padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BalanceSub(label = stringResource(R.string.finance_income), amount = income, tint = Color.White.copy(alpha = 0.22f), icon = Icons.Rounded.ArrowUpward, modifier = Modifier.weight(1f))
                VerticalDivider(
                    modifier = Modifier.height(34.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.22f)
                )
                BalanceSub(
                    label = stringResource(R.string.finance_expenses),
                    amount = expense,
                    tint = Color.White.copy(alpha = 0.22f),
                    icon = Icons.Rounded.ArrowDownward,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
            }
        }
    }
}

private fun currentMonthLabel(): String =
    java.text.SimpleDateFormat("MMMM", java.util.Locale.getDefault()).format(java.util.Date())

@Composable
private fun BalanceSub(
    label: String,
    amount: Double,
    tint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(tint),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.75f))
            Text(
                text = Formatters.currencyShort(amount),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun FinanceRow(e: FinanceEntryEntity, modifier: Modifier = Modifier) {
    val colors = LocalMahalluColors.current
    val isIncome = e.type == "INCOME"
    val accent = if (isIncome) colors.successDark else colors.rose
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isIncome) colors.successTint else colors.roseTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Payments,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(e.description, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.finance_row_detail, categoryLabel(e.category), paymentLabel(e.paymentMethod), Formatters.date(e.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${if (isIncome) "+" else "-"}${Formatters.currencyShort(e.amount)}",
                style = MaterialTheme.typography.titleSmall,
                color = if (isIncome) colors.successDark else colors.textPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private fun shareFinance(context: android.content.Context, state: FinanceUiState) {
    runCatching {
        val text = buildString {
            append(context.getString(R.string.finance_share_summary_header, state.mahalluName.ifBlank { context.getString(R.string.finance_title) }))
            append(context.getString(R.string.finance_share_balance, Formatters.currency(state.balance)))
            append(context.getString(R.string.finance_share_income, Formatters.currency(state.totalIncome)))
            append(context.getString(R.string.finance_share_expenses, Formatters.currency(state.totalExpense)))
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.finance_share_chooser)))
    }
}
