package com.pocketvibe.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketvibe.data.TransactionRepository
import com.pocketvibe.data.local.Category
import com.pocketvibe.data.local.TransactionEntity
import com.pocketvibe.data.local.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class MonthlyViewModel(repo: TransactionRepository) : ViewModel() {

    private val now = Calendar.getInstance()

    val selectedMonth = MutableStateFlow(now.get(Calendar.MONTH) + 1)
    val selectedYear = MutableStateFlow(now.get(Calendar.YEAR))

    val monthTransactions: StateFlow<List<TransactionEntity>> =
        combine(selectedYear, selectedMonth) { y, m -> y to m }
            .flatMapLatest { (y, m) -> repo.transactionsFor(y, m) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Gastos agregados por categoría para el gráfico. */
    val expensesByCategory: StateFlow<Map<Category, Double>> = monthTransactions
        .map { list ->
            list.filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { (_, l) -> l.sumOf { kotlin.math.abs(it.amount) } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
