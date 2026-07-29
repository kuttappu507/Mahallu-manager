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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusLg
import com.mahallu.manager.core.ui.theme.RadiusMd

/**
 * Generic card container with soft shadow and rounded corners.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalMahalluColors.current.surface,
    cornerRadius: Dp = RadiusLg,
    elevation: Dp = 2.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    borderColor: Color? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.05f),
            spotColor = Color.Black.copy(alpha = 0.05f)
        )
        .clip(shape)
        .background(backgroundColor)
        .then(
            if (borderColor != null) Modifier.border(1.dp, borderColor, shape)
            else Modifier
        )
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(contentPadding)

    Box(modifier = baseModifier) { content() }
}

/**
 * List card with title, subtitle, leading icon, and trailing chevron.
 */
@Composable
fun ListItemCard(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingBackground: Color = LocalMahalluColors.current.surfaceVariant,
    leadingTint: Color = LocalMahalluColors.current.primaryIndigo,
    trailingText: String? = null,
    trailingIcon: ImageVector? = Icons.Rounded.ArrowForward,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RadiusMd.let { RoundedCornerShape(it.value) })
                        .background(leadingBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = leadingTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = LocalMahalluColors.current.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalMahalluColors.current.textSecondary,
                        maxLines = 2
                    )
                }
            }
            if (!trailingText.isNullOrBlank()) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.labelLarge,
                    color = LocalMahalluColors.current.primaryIndigo,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
            }
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = LocalMahalluColors.current.textTertiary
                )
            }
        }
    }
}

/**
 * Stat tile used on dashboards: small label + large value.
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color = LocalMahalluColors.current.primaryIndigo,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = LocalMahalluColors.current.textSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = LocalMahalluColors.current.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}