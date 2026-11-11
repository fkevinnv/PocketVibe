package com.pocketvibe.ui.components

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.OvershootInterpolator
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * FAB central con menú tipo "dial": el botón principal rota, las acciones
 * secundarias aparecen en cascada con efecto "pop" y un oscurecido cubre
 * el fondo. Usar como overlay: Modifier.fillMaxSize() desde el caller.
 */
@Composable
fun DialFab(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fabRotation"
    )
    val scrimAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.6f else 0f,
        animationSpec = tween(300),
        label = "scrim"
    )

    Box(modifier = modifier) {

        if (scrimAlpha > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = false }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DialAction(
                visible = expanded,
                delay = 120,
                icon = { Text("📷", style = MaterialTheme.typography.titleLarge) },
                color = Color(0xFF818CF8),
                label = "Escanear ticket",
                onClick = { expanded = false; onScan() }
            )
            DialAction(
                visible = expanded,
                delay = 60,
                icon = { Icon(Icons.Default.Remove, contentDescription = "Gasto") },
                color = Color(0xFFF87171),
                label = "Gasto",
                onClick = { expanded = false; onAddExpense() }
            )
            DialAction(
                visible = expanded,
                delay = 0,
                icon = { Icon(Icons.Default.AddCard, contentDescription = "Ingreso") },
                color = Color(0xFF4ADE80),
                label = "Ingreso",
                onClick = { expanded = false; onAddIncome() }
            )

            FloatingActionButton(
                onClick = { expanded = !expanded },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .scale(
                        animateFloatAsState(
                            targetValue = if (expanded) 1.1f else 1f,
                            label = "fabScale"
                        ).value
                    )
                    .rotate(rotation)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir", tint = Color(0xFF0F0F1A))
            }
        }
    }
}

@Composable
private fun DialAction(
    visible: Boolean,
    delay: Int,
    icon: @Composable () -> Unit,
    color: Color,
    label: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300, delayMillis = delay, easing = Easing { x ->
            OvershootInterpolator().getInterpolation(x)
        }),
        label = "dialScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(200, delayMillis = delay),
        label = "dialAlpha"
    )

    if (alpha > 0f) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
            FloatingActionButton(
                onClick = onClick,
                shape = CircleShape,
                containerColor = color,
                modifier = Modifier.size(48.dp)
            ) { icon() }
        }
    }
}
