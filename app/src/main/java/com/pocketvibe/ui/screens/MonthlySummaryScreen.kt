package com.pocketvibe.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketvibe.ui.components.AnimatedBarChart
import com.pocketvibe.ui.components.AnimatedDonutChart
import com.pocketvibe.vm.MonthlyViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlySummaryScreen(viewModel: MonthlyViewModel) {
    val expenses by viewModel.expensesByCategory.collectAsStateWithLifecycle()
    val selected by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            "Resumen mensual",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Selector de mes horizontal
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items((1..12).toList(), key = { it }) { month ->
                val isSelected = month == selected
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "monthScale"
                )
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedMonth.value = month },
                    label = {
                        Text(
                            Month.of(month)
                                .getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                                .replaceFirstChar { it.uppercase() }
                        )
                    },
                    modifier = Modifier.scale(scale)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Gráficos con crossfade al cambiar de mes
        Crossfade(targetState = expenses, label = "chartCrossfade") { data ->
            when {
                data.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Sin gastos este mes 🎉") }

                else -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedDonutChart(
                        data = data,
                        modifier = Modifier.size(260.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    AnimatedBarChart(
                        data = data,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            }
        }
    }

    // Auto-scroll al mes seleccionado
    LaunchedEffect(selected) {
        listState.animateScrollToItem((selected - 1).coerceAtLeast(0))
    }
}
