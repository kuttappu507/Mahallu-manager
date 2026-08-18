package com.mahallu.manager.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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

@Composable
fun ScreenPageHeader(
    title: String,
    modifier: Modifier = Modifier,
    pillText: String? = null,
    pillColor: Color = LocalMahalluColors.current.primaryIndigo,
    pillBackground: Color = LocalMahalluColors.current.primaryIndigo.copy(alpha = 0.10f),
    trailingIcon: ImageVector = Icons.Rounded.Add,
    onTrailingClick: (() -> Unit)? = null
) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        if (pillText != null) {
            Spacer(Modifier.width(10.dp))
            Text(
                text = pillText,
                style = MaterialTheme.typography.labelSmall,
                color = pillColor,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(pillBackground)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        if (onTrailingClick != null) {
            IconCircleButton(
                icon = trailingIcon,
                onClick = onTrailingClick,
                backgroundColor = colors.surfaceVariant,
                tint = colors.textPrimary
            )
        }
    }
}
