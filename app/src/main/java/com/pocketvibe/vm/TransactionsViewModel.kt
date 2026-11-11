package com.pocketvibe.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketvibe.data.TransactionRepository
import com.pocketvibe.data.local.Category
import com.pocketvibe.data.local.TransactionEntity
import com.pocketvibe.data.local.TransactionType
import com.pocketvibe.data.local.TxSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

data class TransactionsUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val recent: List<TransactionEntity> = emptyList()
)

class TransactionsViewModel(private val repo: TransactionRepository) : ViewModel() {

    val uiState: StateFlow<TransactionsUiState> = repo.allTransactions
        .map { TransactionsUiState(transactions = it, recent = it.take(5)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionsUiState())

    fun add(
        amount: Double,
        type: TransactionType,
        category: Category,
        note: String,
        source: TxSource = TxSource.MANUAL
    ) = viewModelScope.launch {
        repo.addTransaction(
            TransactionEntity(
                amount = if (type == TransactionType.EXPENSE) -abs(amount) else amount,
                type = type,
                category = category,
                note = note,
                source = source
            )
        )
    }

    fun remove(tx: TransactionEntity) = viewModelScope.launch { repo.deleteTransaction(tx) }
}
