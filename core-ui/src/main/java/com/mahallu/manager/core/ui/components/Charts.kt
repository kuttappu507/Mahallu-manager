package com.mahallu.manager.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.R
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import kotlin.math.max

/**
 * Lightweight custom line/area chart for analytics — no external chart dep required.
 */
data class ChartPoint(val label: String, val value: Float)

@Composable
fun LineChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = LocalMahalluColors.current.primaryIndigo,
    fillColor: Color = LocalMahalluColors.current.primaryIndigo.copy(alpha = 0.12f),
    gridColor: Color = LocalMahalluColors.current.chartGrid,
    showValues: Boolean = false
) {
    if (points.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_data), style = MaterialTheme.typography.bodyMedium, color = LocalMahalluColors.current.textTertiary)
        }
        return
    }
    val maxValue = remember(points) { max(points.maxOf { it.value }, 1f) }
    val progress = animatedFloat(1f)

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .graphicsLayer {
                    scaleY = progress.coerceIn(0f, 1f)
                    transformOrigin = TransformOrigin(0f, 1f)
                    alpha = progress.coerceIn(0f, 1f)
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val padding = 16f
                val chartWidth = w - padding * 2
                val chartHeight = h - padding * 2

                // Grid
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = padding + (chartHeight * i / gridLines)
                    drawLine(
                        color = gridColor,
                        start = Offset(padding, y),
                        end = Offset(w - padding, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }

                if (points.size < 2) return@Canvas

                val stepX = chartWidth / (points.size - 1)
                val path = Path()
                val areaPath = Path()

                points.forEachIndexed { idx, p ->
                    val x = padding + stepX * idx
                    val ratio = p.value / maxValue
                    val y = padding + chartHeight - (chartHeight * ratio)
                    if (idx == 0) {
                        path.moveTo(x, y)
                        areaPath.moveTo(x, padding + chartHeight)
                        areaPath.lineTo(x, y)
                    } else {
                        // Smooth bezier between previous and current
                        val prevX = padding + stepX * (idx - 1)
                        val prevRatio = points[idx - 1].value / maxValue
                        val prevY = padding + chartHeight - (chartHeight * prevRatio)
                        val midX = (prevX + x) / 2f
                        path.cubicTo(midX, prevY, midX, y, x, y)
                        areaPath.cubicTo(midX, prevY, midX, y, x, y)
                    }
                    if (idx == points.size - 1) {
                        areaPath.lineTo(x, padding + chartHeight)
                        areaPath.close()
                    }
                    drawCircle(color = lineColor, radius = 4f, center = Offset(x, y))
                }

                drawPath(areaPath, color = fillColor)
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.5f)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { p ->
                Text(
                    text = p.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalMahalluColors.current.textSecondary
                )
            }
        }
    }
}

@Composable
fun BarChart(
    bars: List<ChartPoint>,
    modifier: Modifier = Modifier,
    primaryColor: Color = LocalMahalluColors.current.primaryIndigo,
    secondaryColor: Color = LocalMahalluColors.current.accentCoral,
    showLegend: Boolean = true,
    seriesLabels: Pair<String, String>? = null,
    secondaryBars: List<ChartPoint>? = null
) {
    if (bars.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_data), style = MaterialTheme.typography.bodyMedium, color = LocalMahalluColors.current.textTertiary)
        }
        return
    }
    Column(modifier = modifier) {
        if (showLegend && seriesLabels != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LegendDot(label = seriesLabels.first, color = primaryColor)
                LegendDot(label = seriesLabels.second, color = secondaryColor)
            }
        }
        val progress = animatedFloat(1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .graphicsLayer {
                    scaleY = progress.coerceIn(0f, 1f)
                    transformOrigin = TransformOrigin(0f, 1f)
                    alpha = progress.coerceIn(0f, 1f)
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val padding = 12f
                val chartWidth = w - padding * 2
                val chartHeight = h - padding * 2
                val groupWidth = chartWidth / bars.size
                val barWidth = groupWidth / 3f

                bars.forEachIndexed { idx, bar ->
                    val x = padding + groupWidth * idx + groupWidth / 2f
                    val ratio = bar.value / max(bars.maxOf { it.value }, 1f)
                    val barHeight = chartHeight * ratio

                    // primary
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(x - barWidth - 4f, padding + chartHeight - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    // secondary (real data when provided, otherwise 50% of primary for visual variety)
                    val secValue = secondaryBars?.getOrNull(idx)?.value ?: (bar.value * 0.55f)
                    val secRatio = secValue / max(bars.maxOf { it.value }, 1f)
                    val sec = chartHeight * secRatio
                    drawRoundRect(
                        color = secondaryColor,
                        topLeft = Offset(x + 4f, padding + chartHeight - sec),
                        size = Size(barWidth, sec),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalMahalluColors.current.textSecondary
                )
            }
        }
    }
}

@Composable
fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = LocalMahalluColors.current.textSecondary)
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = LocalMahalluColors.current.primaryIndigo,
    trackColor: Color = LocalMahalluColors.current.surfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                .background(color)
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = LocalMahalluColors.current.textSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = LocalMahalluColors.current.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}