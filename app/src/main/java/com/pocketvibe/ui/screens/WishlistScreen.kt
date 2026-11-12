package com.pocketvibe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    var showAddDialog by remember { mutableStateOf(false) }

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
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
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

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir objetivo")
        }

        CelebrationOverlay(trigger = showCelebration)
    }

    if (showAddDialog) {
        AddWishDialog(
            onConfirm = { name, emoji, target ->
                viewModel.addItem(name, emoji, target)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun AddWishDialog(
    onConfirm: (name: String, emoji: String, target: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🎯") }
    var target by remember { mutableStateOf("") }

    val parsedTarget = target.replace(',', '.').toDoubleOrNull()
    val canCreate = name.isNotBlank() && parsedTarget != null && parsedTarget > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo objetivo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { emoji = it.take(4) },
                    label = { Text("Emoji") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Objetivo (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canCreate,
                onClick = {
                    parsedTarget?.let { onConfirm(name.trim(), emoji.ifBlank { "🎯" }, it) }
                }
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
