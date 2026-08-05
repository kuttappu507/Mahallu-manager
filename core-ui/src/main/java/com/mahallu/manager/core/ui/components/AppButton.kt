package com.mahallu.manager.core.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusFull
import com.mahallu.manager.core.ui.theme.RadiusLg
import com.mahallu.manager.core.ui.theme.RadiusMd

enum class AppButtonStyle { Primary, Secondary, Outline, Ghost, Danger, Coral }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val colors = LocalMahalluColors.current
    val (bg, fg, border) = when (style) {
        AppButtonStyle.Primary -> Triple(colors.primaryIndigo, Color.White, Color.Transparent)
        AppButtonStyle.Secondary -> Triple(colors.surfaceVariant, colors.textPrimary, Color.Transparent)
        AppButtonStyle.Outline -> Triple(Color.Transparent, colors.primaryIndigo, colors.primaryIndigo)
        AppButtonStyle.Ghost -> Triple(Color.Transparent, colors.primaryIndigo, Color.Transparent)
        AppButtonStyle.Danger -> Triple(colors.error, Color.White, Color.Transparent)
        AppButtonStyle.Coral -> Triple(colors.accentCoral, Color.White, Color.Transparent)
    }
    val animatedBg by animateColorAsState(if (enabled) bg else bg.copy(alpha = 0.4f), label = "btn-bg")
    val animatedFg by animateColorAsState(if (enabled) fg else fg.copy(alpha = 0.6f), label = "btn-fg")

    val shape = RoundedCornerShape(RadiusMd.value.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(0.dp)
            .clip(shape)
            .background(animatedBg)
            .then(
                if (border != Color.Transparent) Modifier.border(1.dp, border, shape)
                else Modifier
            )
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = animatedFg,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, tint = animatedFg, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = animatedFg,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    Icon(trailingIcon, contentDescription = null, tint = animatedFg, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun IconCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalMahalluColors.current.surfaceVariant,
    tint: Color = LocalMahalluColors.current.textPrimary,
    size: androidx.compose.ui.unit.Dp = 44.dp,
    iconSize: androidx.compose.ui.unit.Dp = 20.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun ChipPill(
    text: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    accent: Color = LocalMahalluColors.current.primaryIndigo
) {
    val colors = LocalMahalluColors.current
    val bg = if (selected) colors.primaryIndigo.copy(alpha = 0.12f) else colors.surface
    val fg = if (selected) accent else colors.textSecondary
    val borderColor = if (selected) Color.Transparent else colors.border
    val shape = RoundedCornerShape(RadiusFull.value.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bg)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FabAdd(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(8.dp, RoundedCornerShape(RadiusLg.value.dp), ambientColor = LocalMahalluColors.current.accentCoral.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(RadiusLg.value.dp))
            .background(LocalMahalluColors.current.accentCoral)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun SmallActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = LocalMahalluColors.current.primaryIndigo
) {
    val colors = LocalMahalluColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF475569),
            fontWeight = FontWeight.ExtraBold,
            minLines = 2,
            maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}