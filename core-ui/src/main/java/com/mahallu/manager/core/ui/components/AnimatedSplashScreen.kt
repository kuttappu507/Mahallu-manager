package com.mahallu.manager.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahallu.manager.core.ui.R
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

@Composable
fun AnimatedSplashScreen(modifier: Modifier = Modifier) {
    val colors = LocalMahalluColors.current

    val logoScale = remember { Animatable(0.3f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleY = remember { Animatable(40f) }
    val subtitleAlpha = remember { Animatable(0f) }

    val infinite = rememberInfiniteTransition(label = "splash")

    val ringAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )

    val pulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val dot1 by infinite.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2 by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        logoAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        titleAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        titleY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        subtitleAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FF),
                        Color(0xFFFFFFFF),
                        Color(0xFFF8F9FF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(260.dp).scale(pulse)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val r1 = size.width / 2 - 4f
                    val r2 = size.width / 2 - 14f

                    drawArc(
                        color = colors.primaryIndigo.copy(alpha = 0.18f),
                        startAngle = ringAngle,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(center.x - r1, center.y - r1),
                        size = androidx.compose.ui.geometry.Size(r1 * 2, r1 * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = colors.purple.copy(alpha = 0.14f),
                        startAngle = ringAngle + 120f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(center.x - r1, center.y - r1),
                        size = androidx.compose.ui.geometry.Size(r1 * 2, r1 * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = colors.accentCoral.copy(alpha = 0.12f),
                        startAngle = ringAngle + 240f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(center.x - r1, center.y - r1),
                        size = androidx.compose.ui.geometry.Size(r1 * 2, r1 * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = colors.primaryIndigo.copy(alpha = 0.08f),
                        startAngle = -ringAngle,
                        sweepAngle = 80f,
                        useCenter = false,
                        topLeft = Offset(center.x - r2, center.y - r2),
                        size = androidx.compose.ui.geometry.Size(r2 * 2, r2 * 2),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "Mahallu Manager",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Mahallu Manager",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier
                    .alpha(titleAlpha.value)
                    .offset(y = titleY.value.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Community Management",
                fontSize = 14.sp,
                color = colors.textSecondary,
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .offset(y = titleY.value.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors.primaryIndigo.copy(alpha = dot1), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors.primaryIndigo.copy(alpha = dot2), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors.primaryIndigo.copy(alpha = dot1), CircleShape)
                )
            }
        }
    }
}
