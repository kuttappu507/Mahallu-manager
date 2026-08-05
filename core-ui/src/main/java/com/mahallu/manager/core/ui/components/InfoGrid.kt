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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

/**
 * A single label + value cell for a two-column info grid.
 * Keeps label above value with stable spacing and ellipsizes long values.
 */
@Composable
fun InfoCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalMahalluColors.current
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textTertiary,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            maxLines = 1
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * A modern card that lays out label/value pairs in a clean two-column grid
 * separated by thin dividers. Replaces long vertical stacks of InfoRows.
 */
@Composable
fun InfoGridCard(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
) {
    val colors = LocalMahalluColors.current
    AppCard(modifier = modifier, contentPadding = contentPadding) {
        Column {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = 13.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.accentCoral)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            val rows = items.chunked(2)
            rows.forEachIndexed { rowIdx, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    row.forEachIndexed { colIdx, pair ->
                        InfoCell(
                            label = pair.first,
                            value = pair.second,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                if (rowIdx < rows.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.border.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

/**
 * Section title used above detail cards. Matches the Masjidi `.sec-title`:
 * Sora label with a coral accent bar and optional trailing link.
 */
@Composable
fun DetailSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onSeeAll: (() -> Unit)? = null,
    seeAllText: String = "See all"
) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.accentCoral)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary
            )
        }
        if (onSeeAll != null) {
            Spacer(Modifier.weight(1f))
            Text(
                text = seeAllText,
                style = MaterialTheme.typography.labelSmall,
                color = colors.primaryIndigo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSeeAll() }
            )
        }
    }
}

/**
 * Shared section header used on home/dashboard — same coral-bar treatment.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    DetailSectionTitle(
        title = title,
        subtitle = subtitle,
        onSeeAll = onSeeAll,
        modifier = modifier
    )
}

/**
 * A single 3-column detail action (Call / WhatsApp / Edit…). Matches the
 * Masjidi `.d-act`: white card row with a colored `.dic` icon box (38dp,
 * radius 12, 17dp icon) and an 800-weight micro label.
 */
@Composable
fun DetailAction(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalMahalluColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White)
            .border(1.dp, colors.border, RoundedCornerShape(17.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 13.dp),
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF475569),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/**
 * The `.d-acts` 3-column grid used directly under a detail hero.
 */
@Composable
fun DetailActionsRow(
    actions: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        actions.forEach { action ->
            Box(modifier = Modifier.weight(1f)) { action() }
        }
    }
}
