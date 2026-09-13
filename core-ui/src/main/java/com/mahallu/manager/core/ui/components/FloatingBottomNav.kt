package com.mahallu.manager.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

data class FloatingNavItem(
    val icon: ImageVector,
    val route: String,
    val label: String
)

object FloatingNavItems {
    val Home = FloatingNavItem(Icons.Rounded.Dashboard, "dashboard", "Home")
    val Families = FloatingNavItem(Icons.Rounded.FamilyRestroom, "families", "Families")
    val Members = FloatingNavItem(Icons.Rounded.Groups, "members", "Members")
    val Finance = FloatingNavItem(Icons.Rounded.AccountBalanceWallet, "finance", "Finance")
    val More = FloatingNavItem(Icons.Rounded.MoreHoriz, "more", "More")
}

@Composable
fun FloatingBottomNav(
    currentRoute: String,
    onItemClick: (FloatingNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMahalluColors.current
    val items = listOf(
        FloatingNavItems.Home,
        FloatingNavItems.Families,
        FloatingNavItems.Members,
        FloatingNavItems.Finance,
        FloatingNavItems.More
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(50),
                    ambientColor = colors.primaryIndigo.copy(alpha = 0.3f),
                    spotColor = colors.primaryIndigo.copy(alpha = 0.2f)
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colors.primaryIndigo,
                            colors.purple,
                            colors.accentCoral
                        )
                    ),
                    RoundedCornerShape(50)
                )
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(48))
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEach { item ->
                        FloatingNavIcon(
                            item = item,
                            isSelected = currentRoute == item.route,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingNavIcon(
    item: FloatingNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalMahalluColors.current
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.35f else 1f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Canvas(modifier = Modifier.size(48.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.primaryIndigo.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width / 2
                    )
                )
            }
        }

        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) colors.primaryIndigo else colors.textTertiary,
            modifier = Modifier.size(26.dp).scale(iconScale)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(colors.primaryIndigo, colors.purple)
                        )
                    )
            )
        }
    }
}
