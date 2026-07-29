package com.mahallu.manager.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

/**
 * Dashboard header with greeting + actions.
 */
@Composable
fun DashboardHeader(
    greeting: String,
    subtitle: String,
    userName: String,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    notificationCount: Int = 0,
    onProfileClick: () -> Unit
) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
        IconCircleButton(
            icon = Icons.Rounded.Search,
            onClick = onSearchClick,
            backgroundColor = colors.surfaceVariant,
            tint = colors.textPrimary
        )
        Spacer(Modifier.width(8.dp))
        Box {
            IconCircleButton(
                icon = Icons.Rounded.Notifications,
                onClick = onNotificationClick,
                backgroundColor = colors.surfaceVariant,
                tint = colors.textPrimary
            )
            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(start = 28.dp, top = 8.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(colors.accentCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (notificationCount > 9) "9+" else notificationCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.primaryIndigo)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}