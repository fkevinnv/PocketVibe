package com.pocketvibe.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketvibe.data.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class DashboardUiState(
    val remaining: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = true
)

class DashboardViewModel(repo: TransactionRepository) : ViewModel() {

    private val now = Calendar.getInstance()
    private val year = now.get(Calendar.YEAR)
    private val month = now.get(Calendar.MONTH) + 1

    val uiState: StateFlow<DashboardUiState> =
        repo.monthlyTotals(year, month)
            .map { t ->
                DashboardUiState(
                    remaining = t.income - t.expense,
                    totalIncome = t.income,
                    totalExpense = t.expense,
                    isLoading = false
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}
