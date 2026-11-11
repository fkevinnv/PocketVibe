package com.pocketvibe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pocketvibe.ui.components.CelebrationOverlay
import com.pocketvibe.ui.components.WishCard
import com.pocketvibe.vm.WishlistViewModel
import kotlinx.coroutines.delay

@Composable
fun WishlistScreen(viewModel: WishlistViewModel) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    var showCelebration by remember { mutableStateOf(false) }

    // Confeti al completar un objetivo (una sola vez por item)
    LaunchedEffect(Unit) {
        viewModel.justCompleted.collect {
            showCelebration = true
        }
    }

    // Apaga el confeti tras la animación
    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(2000)
            showCelebration = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            Text(
                "Añade tu primer objetivo de ahorro ✨",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    WishCard(
                        item = item,
                        onContribute = { viewModel.contribute(item, 50.0) }
                    )
                }
            }
        }

        CelebrationOverlay(trigger = showCelebration)
    }
}
