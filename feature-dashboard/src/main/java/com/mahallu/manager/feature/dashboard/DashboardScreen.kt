package com.mahallu.manager.feature.dashboard

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.repository.ActivityItem
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.BarChart
import com.mahallu.manager.core.ui.components.ChartPoint
import com.mahallu.manager.core.ui.components.DashboardHeader
import com.mahallu.manager.core.ui.components.LineChart
import com.mahallu.manager.core.ui.components.SmallActionButton
import com.mahallu.manager.core.ui.components.StatTile
import com.mahallu.manager.core.ui.components.statusBarInsetDp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import feature.dashboard.feature.dashboard.R

private val STAT_OVERLAP_FALLBACK = 56.dp

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    DashboardLightStatusBar()

    val statusBarInset = statusBarInsetDp()
    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = -statusBarInset),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column {
                    var gridHeightPx by remember { mutableStateOf(0) }
                    val density = LocalDensity.current
                    val overlap = if (gridHeightPx > 0) {
                        with(density) { (gridHeightPx / 2).toDp() }
                    } else {
                        STAT_OVERLAP_FALLBACK
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        DashboardHeader(
                            greeting = stringResource(R.string.dashboard_greeting),
                            subtitle = Formatters.dateWithWeekday(System.currentTimeMillis()),
                            userName = state.userName,
                            onSearchClick = { onNavigate("search") },
                            onNotificationClick = { onNavigate("announcements") }
                        )
                        StatsGrid(
                            state = state,
                            colors = colors,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = overlap)
                                .onSizeChanged { gridHeightPx = it.height }
                        )
                    }
                    Spacer(Modifier.height(overlap))
                }
            }
            item {
                QuickActionsRow(onNavigate)
                Spacer(Modifier.height(8.dp))
            }
            item {
                SectionHeader(stringResource(R.string.dashboard_donations_last_6), onMore = { onNavigate("donations") })
                ChartCard(
                    title = stringResource(R.string.dashboard_donation_trends),
                    subtitle = trendRangeLabel(state.donationTrend.map { it.label }),
                    total = Formatters.currencyShort(state.donationTrend.sumOf { it.amount.toDouble() }, context),
                    points = state.donationTrend.map { ChartPoint(it.label, it.amount) },
                    onClick = { onNavigate("donations") }
                ) {
                    LineChart(
                        points = state.donationTrend.map { ChartPoint(it.label, it.amount) },
                        lineColor = colors.primaryIndigo,
                        fillColor = colors.primaryIndigo.copy(alpha = 0.12f),
                        gridColor = colors.chartGrid
                    )
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                SectionHeader(stringResource(R.string.dashboard_income_vs_expense), subtitle = stringResource(R.string.dashboard_this_year), onMore = { onNavigate("finance") })
                ChartCard(
                    title = stringResource(R.string.dashboard_income_vs_expense),
                    subtitle = stringResource(R.string.dashboard_monthly_net),
                    total = Formatters.currencyShort(
                        state.monthlyTrend.sumOf { it.income - it.expense },
                        context
                    ),
                    points = state.monthlyTrend.map { ChartPoint(it.label, it.income.toFloat()) },
                    onClick = { onNavigate("finance") }
                ) {
                    BarChart(
                        bars = state.monthlyTrend.map { ChartPoint(it.label, it.income.toFloat()) },
                        primaryColor = colors.chartIncome,
                        secondaryColor = colors.chartExpense,
                        showLegend = true,
                        seriesLabels = stringResource(R.string.dashboard_income) to stringResource(R.string.dashboard_expense),
                        secondaryBars = state.monthlyTrend.map { ChartPoint(it.label, it.expense.toFloat()) }
                    )
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                SectionHeader(stringResource(R.string.dashboard_recent_activity), subtitle = stringResource(R.string.dashboard_last_7_days), onMore = { onNavigate("finance") })
                RecentActivities(state.summary.recentActivities, context)
            }
        }
    }
}

@Composable
private fun StatsGrid(
    state: DashboardUiState,
    colors: com.mahallu.manager.core.ui.theme.MahalluColors,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatTile(
            label = stringResource(R.string.dashboard_stat_members),
            value = state.summary.totalMembers.toString(),
            icon = Icons.Rounded.Groups,
            accent = colors.primaryIndigo,
            index = 0,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = stringResource(R.string.dashboard_stat_families),
            value = state.summary.totalFamilies.toString(),
            icon = Icons.Rounded.FamilyRestroom,
            accent = colors.purple,
            index = 1,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = stringResource(R.string.dashboard_stat_this_month),
            value = Formatters.currencyShort(state.summary.collectionThisMonth, LocalContext.current),
            icon = Icons.Rounded.MonetizationOn,
            accent = colors.success,
            index = 2,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = stringResource(R.string.dashboard_stat_certificates),
            value = state.summary.certificateCount.toString(),
            icon = Icons.Rounded.EmojiEvents,
            accent = colors.warning,
            index = 3,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionsRow(onNavigate: (String) -> Unit) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickActions().forEach { action ->
                SmallActionButton(
                    icon = action.icon,
                    label = stringResource(action.labelRes),
                    onClick = { onNavigate(action.route) },
                    accent = action.accent ?: colors.primaryIndigo,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class QuickAction(
    val labelRes: Int,
    val icon: ImageVector,
    val route: String,
    val accent: Color? = null
)

private fun quickActions(): List<QuickAction> = listOf(
    QuickAction(R.string.dashboard_action_add_member, Icons.Rounded.PersonAdd, "member_edit?id=", Color(0xFF4F46E5)),
    QuickAction(R.string.dashboard_action_record_donation, Icons.Rounded.VolunteerActivism, "donation_entry", Color(0xFF059669)),
    QuickAction(R.string.dashboard_action_add_collection, Icons.Rounded.Payments, "collection_entry?memberId=", Color(0xFF0D9488)),
    QuickAction(R.string.dashboard_action_new_certificate, Icons.Rounded.AddCard, "certificates", Color(0xFF7C3AED))
)

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    total: String,
    points: List<ChartPoint>,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.muted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = total,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    TrendChip(points)
                }
            }
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun TrendChip(points: List<ChartPoint>) {
    val colors = LocalMahalluColors.current
    val up = points.size >= 2 && points.last().value >= points.first().value
    val pct = if (points.size >= 2 && points.first().value != 0f) {
        (points.last().value - points.first().value) / points.first().value * 100f
    } else {
        0f
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (up) colors.successTint else colors.roseTint)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = if (up) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
            contentDescription = null,
            tint = if (up) colors.successDark else colors.rose,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = "${if (up) "+" else ""}%.1f%%".format(pct),
            style = MaterialTheme.typography.labelSmall,
            color = if (up) colors.successDark else colors.rose,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun trendRangeLabel(labels: List<String>): String =
    if (labels.size >= 2) "${labels.first()} – ${labels.last()}" else labels.firstOrNull() ?: ""

/**
 * Makes the status bar transparent with light (white) icons while the dashboard's
 * indigo gradient header is on screen, matching the Masjidi `.sbar.light` treatment.
 * Captures the previous appearance and restores it on dispose so other screens
 * (light backgrounds) keep dark icons.
 */
@Composable
private fun DashboardLightStatusBar() {
    val view = LocalView.current
    val activity = view.context as? android.app.Activity
    DisposableEffect(Unit) {
        val window = activity?.window ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(window, view)
        val prevLight = controller.isAppearanceLightStatusBars
        val prevColor = window.statusBarColor
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        controller.isAppearanceLightStatusBars = false
        onDispose {
            window.statusBarColor = prevColor
            controller.isAppearanceLightStatusBars = prevLight
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String? = null, onMore: (() -> Unit)? = null) {    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.accentCoral)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textTertiary
                )
            }
        }
        if (onMore != null) {
            Text(
                text = stringResource(R.string.dashboard_see_all),
                style = MaterialTheme.typography.labelSmall,
                color = colors.primaryIndigo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onMore() }
            )
        }
    }
}

@Composable
private fun RecentActivities(activities: List<ActivityItem>, context: Context) {
    val colors = LocalMahalluColors.current
    if (activities.isEmpty()) {
        AppCard(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(20.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_no_recent),
                color = colors.textTertiary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
        activities.take(8).forEach { item ->
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp),
                cornerRadius = 16.dp,
                contentPadding = PaddingValues(horizontal = 15.dp, vertical = 13.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Handshake,
                            contentDescription = null,
                            tint = colors.primaryIndigo,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = Formatters.relativeDate(item.timestamp, context),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}