package com.mahallu.manager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mahallu.manager.core.ui.components.AppBottomNavBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.feature.dashboard.DashboardScreen
import com.mahallu.manager.feature.families.FamiliesScreen
import com.mahallu.manager.feature.members.MembersScreen
import com.mahallu.manager.feature.finance.FinanceScreen
import com.mahallu.manager.feature.settings.MoreScreen

@Composable
fun MainShell(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("dashboard") }
    val tabs = remember {
        listOf(
            BottomTab("dashboard", "Home", Icons.Rounded.Dashboard),
            BottomTab("families", "Families", Icons.Rounded.FamilyRestroom),
            BottomTab("members", "Members", Icons.Rounded.Groups),
            BottomTab("finance", "Finance", Icons.Rounded.AccountBalanceWallet),
            BottomTab("more", "More", Icons.Rounded.MoreHoriz)
        )
    }

    Scaffold(
        bottomBar = {
            AppBottomNavBar(
                currentRoute = selectedTab,
                onItemClick = { item -> selectedTab = item.route }
            )
        },
        containerColor = LocalMahalluColors.current.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                "dashboard" -> DashboardScreen(
                    onNavigate = { route ->
                        if (tabs.any { it.route == route }) selectedTab = route
                    }
                )
                "families" -> FamiliesScreen(
                    onAddFamily = {},
                    onFamilyClick = {}
                )
                "members" -> MembersScreen(
                    onAddMember = {},
                    onMemberClick = {}
                )
                "finance" -> FinanceScreen(onAddEntry = {})
                "more" -> MoreScreen(
                    onNavigate = {},
                    onLogout = onLogout
                )
            }
        }
    }
}

data class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
