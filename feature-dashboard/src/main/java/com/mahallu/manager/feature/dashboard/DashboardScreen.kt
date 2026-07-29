package com.mahallu.manager.feature.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.repository.ActivityItem
import com.mahallu.manager.core.database.repository.CollectionTrendPoint
import com.mahallu.manager.core.database.repository.MonthlyTrendPoint
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.BarChart
import com.mahallu.manager.core.ui.components.ChartPoint
import com.mahallu.manager.core.ui.components.DashboardHeader
import com.mahallu.manager.core.ui.components.LineChart
import com.mahallu.manager.core.ui.components.SmallActionButton
import com.mahallu.manager.core.ui.components.StatTile
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                DashboardHeader(
                    greeting = Formatters.greeting(),
                    subtitle = "${state.role.replaceFirstChar { it.uppercase() }} • ${state.userName}",
                    userName = state.userName,
                    onSearchClick = { onNavigate("search") },
                    onNotificationClick = { },
                    onProfileClick = { }
                )
            }
            item {
                StatsGrid(state, colors)
            }
            item {
                Spacer(Modifier.height(8.dp))
                QuickActionsRow(onNavigate)
                Spacer(Modifier.height(8.dp))
            }
            item {
                SectionHeader("Monthly Collection", subtitle = "This Year", onMore = { onNavigate("subscriptions") })
                AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                    LineChart(
                        points = state.collectionTrend.map { ChartPoint(it.label, it.amount) },
                        lineColor = colors.primaryIndigo,
                        fillColor = colors.primaryIndigo.copy(alpha = 0.12f),
                        gridColor = colors.chartGrid
                    )
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                SectionHeader("Income vs Expense", subtitle = "This Year", onMore = { onNavigate("finance") })
                AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                    BarChart(
                        bars = state.monthlyTrend.map { ChartPoint(it.label, it.income.toFloat()) },
                        primaryColor = colors.chartIncome,
                        secondaryColor = colors.chartExpense,
                        showLegend = true,
                        seriesLabels = "Income" to "Expense"
                    )
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                SectionHeader("Recent Activities", subtitle = "Last 7 days")
                RecentActivities(state.summary.recentActivities)
            }
        }
    }
}

@Composable
private fun StatsGrid(state: DashboardUiState, colors: com.mahallu.manager.core.ui.theme.MahalluColors) {
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = "Families",
                value = state.summary.totalFamilies.toString(),
                icon = Icons.Rounded.FamilyRestroom,
                accent = colors.primaryIndigo,
                modifier = Modifier.weight(1f),
                onClick = { }
            )
            StatTile(
                label = "Members",
                value = state.summary.totalMembers.toString(),
                icon = Icons.Rounded.Groups,
                accent = colors.info,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Collection (This Month)",
                value = Formatters.currencyShort(state.summary.collectionThisMonth),
                icon = Icons.Rounded.MonetizationOn,
                accent = colors.success,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                label = "Pending Dues",
                value = Formatters.currencyShort(state.summary.pendingDues),
                icon = Icons.Rounded.PendingActions,
                accent = colors.warning,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Donations",
                value = Formatters.currencyShort(state.summary.donationsThisMonth),
                icon = Icons.Rounded.VolunteerActivism,
                accent = colors.accentCoral,
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Welfare Beneficiaries",
                value = state.summary.welfareBeneficiaries.toString(),
                icon = Icons.Rounded.HealthAndSafety,
                accent = colors.success,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionsRow(onNavigate: (String) -> Unit) {
    val colors = LocalMahalluColors.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(quickActions()) { action ->
            SmallActionButton(
                icon = action.icon,
                label = action.label,
                onClick = { onNavigate(action.route) },
                accent = action.accent ?: colors.primaryIndigo
            )
        }
    }
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val accent: Color? = null
)

private fun quickActions(): List<QuickAction> = listOf(
    QuickAction("Add Family", Icons.Rounded.FamilyRestroom, "family_edit?id="),
    QuickAction("Add Member", Icons.Rounded.PersonAdd, "member_edit?id="),
    QuickAction("Record Payment", Icons.Rounded.ReceiptLong, "collection_entry?memberId="),
    QuickAction("Add Donation", Icons.Rounded.VolunteerActivism, "donation_entry"),
    QuickAction("More", Icons.Rounded.AddCard, "more")
)

@Composable
private fun SectionHeader(title: String, subtitle: String? = null, onMore: (() -> Unit)? = null) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary
                )
            }
        }
        if (onMore != null) {
            Text(
                text = "View all",
                style = MaterialTheme.typography.labelLarge,
                color = colors.primaryIndigo,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onMore() }
            )
        }
    }
}

@Composable
private fun RecentActivities(activities: List<ActivityItem>) {
    val colors = LocalMahalluColors.current
    if (activities.isEmpty()) {
        AppCard(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(20.dp)
        ) {
            Text(
                text = "No recent activities",
                color = colors.textTertiary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }
    AppCard(
        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {
        Column {
            activities.take(8).forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Handshake,
                            contentDescription = null,
                            tint = colors.primaryIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = Formatters.relativeDate(item.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}