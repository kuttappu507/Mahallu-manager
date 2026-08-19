package com.mahallu.manager.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
import com.mahallu.manager.core.ui.util.Formatters
import feature.settings.feature.settings.R

private data class MenuItem(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val tint: Color,
    val tintBackground: Color
)

private data class MenuGroup(
    val title: String,
    val items: List<MenuItem>
)

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val colors = LocalMahalluColors.current
    val vm: SettingsViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    val groups = listOf(
        MenuGroup(
            stringResource(R.string.more_group_records),
            listOf(
                MenuItem(stringResource(R.string.more_menu_subscriptions), stringResource(R.string.more_menu_subscriptions_sub), Icons.Rounded.MonetizationOn, "subscriptions", colors.primaryIndigo, colors.indigoTint),
                MenuItem(stringResource(R.string.more_menu_donations), stringResource(R.string.more_menu_donations_sub), Icons.Rounded.VolunteerActivism, "donations", colors.successDark, colors.successTint),
                MenuItem(stringResource(R.string.more_menu_marriages), stringResource(R.string.more_menu_marriages_sub), Icons.Rounded.Favorite, "marriages", colors.rose, colors.roseTint),
                MenuItem(stringResource(R.string.more_menu_deaths), stringResource(R.string.more_menu_deaths_sub), Icons.Rounded.HistoryEdu, "deaths", colors.textSecondary, colors.surfaceVariant),
                MenuItem(stringResource(R.string.more_menu_welfare), stringResource(R.string.more_menu_welfare_sub), Icons.Rounded.HealthAndSafety, "welfare", colors.purple, colors.purpleTint)
            )
        ),
        MenuGroup(
            stringResource(R.string.more_group_data_tools),
            listOf(
                MenuItem(stringResource(R.string.more_menu_certificates), stringResource(R.string.more_menu_certificates_sub), Icons.Rounded.Description, "certificates", colors.primaryIndigo, colors.indigoTint),
                MenuItem(stringResource(R.string.more_menu_reports), stringResource(R.string.more_menu_reports_sub), Icons.Rounded.Assessment, "reports", colors.purple, colors.purpleTint),
                MenuItem(stringResource(R.string.more_menu_backup), stringResource(R.string.more_menu_backup_sub), Icons.Rounded.Backup, "backup", colors.warning, colors.warningTint),
                MenuItem(stringResource(R.string.more_menu_settings), stringResource(R.string.more_menu_settings_sub), Icons.Rounded.Settings, "settings", colors.textSecondary, colors.surfaceVariant)
            )
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.more_title),
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 10.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    ProfileCard(
                        name = state.userName,
                        role = state.userRole,
                        mahallu = state.mahalluName,
                        totalMembers = state.totalMembers,
                        totalFamilies = state.totalFamilies,
                        totalCertificates = state.totalCertificates
                    )
                }
                item { MenuCard(groups = groups, onNavigate = onNavigate, onLogout = onLogout) }
                item {
                    Text(
                        text = stringResource(R.string.more_version_footer, state.mahalluName.uppercase()),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(name: String, role: String, mahallu: String, totalMembers: Int, totalFamilies: Int, totalCertificates: Int) {
    val colors = LocalMahalluColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(colors = listOf(PrimaryIndigo, colors.primaryDark, colors.purple)))
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(Formatters.initials(name), style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (mahallu.isNotBlank()) Text(mahallu, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.more_mahallu_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primaryIndigo,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clip(RoundedCornerShape(9.dp)).background(Color.White).padding(horizontal = 9.dp, vertical = 5.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().drawBehind {
                    drawLine(color = Color.White.copy(alpha = 0.22f), start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = 1f)
                }.padding(top = 14.dp)
            ) {
                ProfileStat(totalMembers.toString(), stringResource(R.string.more_profile_members), Modifier.weight(1f))
                VerticalDivider(Modifier.height(32.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.22f))
                ProfileStat(totalFamilies.toString(), stringResource(R.string.more_profile_families), Modifier.weight(1f))
                VerticalDivider(Modifier.height(32.dp), thickness = 1.dp, color = Color.White.copy(alpha = 0.22f))
                ProfileStat(totalCertificates.toString(), stringResource(R.string.more_profile_certificates), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.72f), fontWeight = FontWeight.ExtraBold, letterSpacing = MaterialTheme.typography.labelSmall.letterSpacing)
    }
}

@Composable
private fun MenuCard(groups: List<MenuGroup>, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    val colors = LocalMahalluColors.current
    AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(6.dp)) {
        Column {
            groups.forEachIndexed { gIdx, group ->
                if (gIdx > 0) HorizontalDivider(thickness = 1.dp, color = colors.border, modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp))
                Text(group.title.uppercase(), style = MaterialTheme.typography.labelSmall, color = colors.textTertiary, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp, modifier = Modifier.padding(start = 12.dp, top = 14.dp, end = 12.dp, bottom = 6.dp))
                group.items.forEachIndexed { iIdx, item ->
                    MenuRow(item) { onNavigate(item.route) }
                    if (iIdx < group.items.lastIndex) HorizontalDivider(thickness = 1.dp, color = colors.border, modifier = Modifier.padding(horizontal = 10.dp))
                }
            }
            HorizontalDivider(thickness = 1.dp, color = colors.border, modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout).padding(horizontal = 10.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(colors.roseTint), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Logout, contentDescription = null, tint = colors.rose, modifier = Modifier.size(17.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.more_logout), style = MaterialTheme.typography.bodyMedium, color = colors.rose, fontWeight = FontWeight.ExtraBold)
                    Text(stringResource(R.string.more_logout_subtitle), style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun MenuRow(item: MenuItem, onClick: () -> Unit) {
    val colors = LocalMahalluColors.current
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(item.tintBackground), contentAlignment = Alignment.Center) {
            Icon(item.icon, contentDescription = null, tint = item.tint, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.label, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = colors.borderStrong, modifier = Modifier.size(17.dp))
    }
}
