package com.pocketvibe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pocketvibe.data.local.Category
import com.pocketvibe.data.local.TransactionType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    type: TransactionType,
    onSave: (amount: Double, category: Category, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = remember(type) {
        if (type == TransactionType.EXPENSE) {
            listOf(
                Category.FOOD, Category.LEISURE, Category.TRANSPORT,
                Category.SHOPPING, Category.BILLS, Category.HEALTH, Category.OTHER
            )
        } else {
            listOf(Category.SALARY, Category.OTHER)
        }
    }

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selected by remember(type) { mutableStateOf(categories.first()) }

    // Importe válido: número > 0 (admite coma o punto decimal)
    val parsedAmount = amount.replace(',', '.').toDoubleOrNull()
    val canSave = parsedAmount != null && parsedAmount > 0.0

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                if (type == TransactionType.INCOME) "Nuevo ingreso" else "Nuevo gasto",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                label = { Text("Importe (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // FlowRow: los chips saltan de línea en pantallas estrechas
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selected == cat,
                        onClick = { selected = cat },
                        label = { Text("${cat.emoji} ${cat.label}") }
                    )
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Nota (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { parsedAmount?.let { onSave(it, selected, note.trim()) } },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }

            Spacer(Modifier.height(32.dp))
        }
    }
}
