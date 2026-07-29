package com.mahallu.manager.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
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
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

object BottomNavItems {
    val Home = BottomNavItem("Home", Icons.Rounded.Dashboard, "dashboard")
    val Families = BottomNavItem("Families", Icons.Rounded.FamilyRestroom, "families")
    val Members = BottomNavItem("Members", Icons.Rounded.Groups, "members")
    val Finance = BottomNavItem("Finance", Icons.Rounded.AccountBalanceWallet, "finance")
    val More = BottomNavItem("More", Icons.Rounded.MoreHoriz, "more")
}

@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMahalluColors.current
    val items = listOf(
        BottomNavItems.Home,
        BottomNavItems.Families,
        BottomNavItems.Members,
        BottomNavItems.Finance,
        BottomNavItems.More
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavItemView(item = item, isSelected = currentRoute == item.route, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalMahalluColors.current
    val tint by animateColorAsState(
        targetValue = if (isSelected) colors.primaryIndigo else colors.textTertiary,
        label = "nav-tint"
    )
    Column(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 40.dp else 32.dp)
                .clip(CircleShape)
                .background(if (isSelected) colors.primaryIndigo.copy(alpha = 0.12f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}