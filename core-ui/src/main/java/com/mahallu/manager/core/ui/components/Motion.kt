package com.mahallu.manager.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Reveals its content with a subtle fade + slide-up the first time it composes.
 * Use [index] to stagger siblings (0, 1, 2, ...) for a cascading entrance.
 *
 * Content always occupies its full layout space (no size jump); only opacity
 * and a small GPU translation are animated.
 */
@Composable
fun AnimatedReveal(
    modifier: Modifier = Modifier,
    index: Int = 0,
    content: @Composable () -> Unit
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay((index * 35).coerceAtMost(180).toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
    }
    val p = progress.value
    Box(
        modifier = modifier
            .alpha(p)
            .graphicsLayer { translationY = 12.dp.toPx() * (1f - p) }
    ) {
        content()
    }
}

/**
 * Animates a value from 0f to [target] the first time it composes, then keeps it.
 * Useful for count-up numbers and chart draw-in effects.
 */
@Composable
fun animatedFloat(target: Float): Float {
    val animated = remember(target) { Animatable(0f) }
    LaunchedEffect(target) {
        animated.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }
    return animated.value
}
