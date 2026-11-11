package com.pocketvibe.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private class Particle(
    val color: Color,
    val angle: Float,
    val speed: Float,
    val size: Float
)

/** Lluvia de partículas que explota desde el centro al completar un objetivo. */
@Composable
fun CelebrationOverlay(trigger: Boolean, modifier: Modifier = Modifier) {
    if (!trigger) return

    val particles = remember {
        List(80) {
            Particle(
                color = listOf(
                    Color(0xFFF472B6), Color(0xFF4ADE80), Color(0xFF818CF8),
                    Color(0xFFFACC15), Color(0xFF38BDF8)
                ).random(),
                angle = Random.nextFloat() * 2f * Math.PI.toFloat(),
                speed = Random.nextFloat() * 900f + 400f,
                size = Random.nextFloat() * 16f + 8f
            )
        }
    }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1800))
    }

    Canvas(modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val t = progress.value

        particles.forEach { p ->
            val distance = p.speed * t
            val pos = Offset(
                x = center.x + cos(p.angle) * distance,
                y = center.y + sin(p.angle) * distance + 500f * t * t // gravedad
            )
            drawCircle(
                color = p.color.copy(alpha = 1f - t),
                radius = p.size * (1f - t * 0.5f),
                center = pos
            )
        }
    }
}
