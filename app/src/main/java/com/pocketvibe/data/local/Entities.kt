package com.pocketvibe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

enum class Category(val emoji: String, val label: String) {
    FOOD("🍔", "Comida"),
    LEISURE("🎮", "Ocio"),
    TRANSPORT("🚌", "Transporte"),
    SHOPPING("🛍️", "Compras"),
    BILLS("💡", "Facturas"),
    HEALTH("💊", "Salud"),
    SALARY("💼", "Salario"),
    OTHER("📦", "Otro")
}

enum class TxSource { MANUAL, SCAN }

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val source: TxSource = TxSource.MANUAL
)

@Entity(tableName = "wish_items")
data class WishItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (targetAmount <= 0.0) 0f
            else (savedAmount / targetAmount).toFloat().coerceIn(0f, 1f)
}
