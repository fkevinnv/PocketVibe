package com.pocketvibe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pocketvibe.data.local.TransactionEntity
import com.pocketvibe.ui.components.AnimatedCounter
import com.pocketvibe.ui.components.DialFab
import com.pocketvibe.ui.components.TransactionRow
import com.pocketvibe.vm.DashboardUiState

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    recent: List<TransactionEntity>,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onScan: () -> Unit,
    onSeeMonthly: () -> Unit,
    onSeeWishlist: () -> Unit,
    onSeeHistory: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { BalanceHeroCard(state) }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Movimientos recientes", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onSeeHistory) { Text("Ver todo") }
                }
            }

            items(recent, key = { it.id }) { tx ->
                val visibleState = remember {
                    MutableTransitionState(false).apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 },
                    exit = fadeOut()
                ) {
                    TransactionRow(tx)
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = onSeeMonthly) { Text("📊 Resumen mensual") }
                    TextButton(onClick = onSeeWishlist) { Text("⭐ Wishlist") }
                }
            }

            item { Spacer(Modifier.height(96.dp)) }
        }

        // FAB dial como overlay a pantalla completa (incluye el oscurecido)
        DialFab(
            onAddIncome = onAddIncome,
            onAddExpense = onAddExpense,
            onScan = onScan,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun BalanceHeroCard(state: DashboardUiState) {
    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF7C3AED), Color(0xFFEC4899), Color(0xFFF97316))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "Dinero restante",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelLarge
                )
                AnimatedCounter(
                    targetValue = state.remaining,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Ingresos", color = Color.White.copy(alpha = 0.8f))
                        AnimatedCounter(
                            targetValue = state.totalIncome,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFBBF7D0),
                            prefix = "+"
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Gastos", color = Color.White.copy(alpha = 0.8f))
                        AnimatedCounter(
                            targetValue = state.totalExpense,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFECACA),
                            prefix = "-"
                        )
                    }
                }
            }
        }
    }
}
