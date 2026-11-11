package com.pocketvibe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pocketvibe.data.local.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionRow(tx: TransactionEntity) {
    val dateFormat = rememberDateFormat()

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(tx.category.emoji, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(tx.category.label, fontWeight = FontWeight.SemiBold)
            val subtitle = buildString {
                if (tx.note.isNotBlank()) append(tx.note)
                if (tx.source == com.pocketvibe.data.local.TxSource.SCAN) {
                    if (isNotEmpty()) append(" · ")
                    append("📷")
                }
                if (isNotEmpty()) append(" · ")
                append(dateFormat.format(Date(tx.timestamp)))
            }
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "%+.2f €".format(tx.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (tx.amount >= 0) Color(0xFF4ADE80) else Color(0xFFF87171)
        )
    }
}

@Composable
private fun rememberDateFormat() =
    androidx.compose.runtime.remember { SimpleDateFormat("dd MMM", Locale("es", "ES")) }
