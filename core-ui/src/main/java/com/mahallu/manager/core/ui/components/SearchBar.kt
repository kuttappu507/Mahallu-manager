package com.mahallu.manager.core.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusFull
import com.mahallu.manager.core.ui.theme.RadiusLg
import com.mahallu.manager.core.ui.theme.RadiusMd

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle

@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    count: Int? = null
) {
    val colors = LocalMahalluColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 18.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(RadiusMd.value.dp))
            .background(Color.White)
            .border(1.5.dp, colors.border, RoundedCornerShape(RadiusMd.value.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = colors.textPrimary,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = colors.textTertiary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.weight(1f)
            )
            if (count != null) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun TopAppBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    onNotificationClick: (() -> Unit)? = null,
    notificationCount: Int = 0,
    showBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    trailingActions: @Composable (() -> Unit)? = null,
    backgroundColor: Color = LocalMahalluColors.current.background
) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack && onBackClick != null) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                onClick = onBackClick,
                backgroundColor = Color.Transparent,
                tint = colors.textPrimary
            )
            Spacer(Modifier.width(8.dp))
        } else if (onMenuClick != null) {
            IconCircleButton(
                icon = Icons.Rounded.Menu,
                onClick = onMenuClick,
                backgroundColor = Color.Transparent,
                tint = colors.textPrimary
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (onSearchClick != null) {
            IconCircleButton(
                icon = Icons.Rounded.Search,
                onClick = onSearchClick,
                backgroundColor = Color.Transparent,
                tint = colors.textPrimary
            )
            Spacer(Modifier.width(6.dp))
        }
        if (onNotificationClick != null) {
            Box {
                IconCircleButton(
                    icon = Icons.Rounded.Notifications,
                    onClick = onNotificationClick,
                    backgroundColor = Color.Transparent,
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
            Spacer(Modifier.width(6.dp))
        }
        if (trailingActions != null) trailingActions()
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val colors = LocalMahalluColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            AppButton(text = actionLabel, onClick = onAction, style = AppButtonStyle.Primary)
        }
    }
}

@Composable
fun FilterChipsRow(
    items: List<Pair<String, Boolean>>,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = LocalMahalluColors.current.primaryIndigo
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { idx ->
            val (label, selected) = items[idx]
            ChipPill(text = label, selected = selected, onClick = { onToggle(idx) }, accent = accent)
        }
    }
}