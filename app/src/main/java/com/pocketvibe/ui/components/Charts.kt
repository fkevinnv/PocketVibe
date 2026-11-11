package com.pocketvibe.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pocketvibe.data.local.Category
import java.text.NumberFormat
import java.util.Locale

val CategoryPalette: Map<Category, Color> = mapOf(
    Category.FOOD to Color(0xFFFB923C),
    Category.LEISURE to Color(0xFF818CF8),
    Category.TRANSPORT to Color(0xFF38BDF8),
    Category.SHOPPING to Color(0xFFF472B6),
    Category.BILLS to Color(0xFFFACC15),
    Category.HEALTH to Color(0xFF4ADE80),
    Category.SALARY to Color(0xFF34D399),
    Category.OTHER to Color(0xFF94A3B8)
)

/** Donut chart que se "dibuja" con stroke animado al cambiar de datos/mes. */
@Composable
fun AnimatedDonutChart(
    data: Map<Category, Double>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 40f
) {
    val total = data.values.sum().takeIf { it > 0 } ?: 1.0

    // Reinicia el trazo cuando cambian los datos (nuevo mes)
    val sweepAnim = remember { Animatable(0f) }
    LaunchedEffect(data) {
        sweepAnim.snapTo(0f)
        sweepAnim.animateTo(1f, animationSpec = tween(1400))
    }

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "ES")) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            val arcSize = Size(diameter, diameter)

            var startAngle = -90f
            data.forEach { (category, value) ->
                val sweep = (value / total).toFloat() * 360f * sweepAnim.value
                drawArc(
                    color = CategoryPalette[category] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", style = MaterialTheme.typography.labelMedium)
            Text(
                formatter.format(data.values.sum()),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Barras verticales animadas por categoría. */
@Composable
fun AnimatedBarChart(
    data: Map<Category, Double>,
    modifier: Modifier = Modifier
) {
    val maxValue = (data.values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (category, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val height by animateDpAsState(
                    targetValue = (120 * (value / maxValue)).dp,
                    animationSpec = tween(900),
                    label = "barHeight"
                )
                Box(
                    Modifier
                        .width(28.dp)
                        .height(height)
                        .padding(horizontal = 4.dp)
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = CategoryPalette[category] ?: Color.Gray,
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                }
                androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                Text(category.emoji, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
