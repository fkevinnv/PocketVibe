package com.pocketvibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pocketvibe.data.TransactionRepository
import com.pocketvibe.data.local.AppDatabase
import com.pocketvibe.ui.navigation.AppNavHost
import com.pocketvibe.ui.theme.PocketVibeTheme
import com.pocketvibe.vm.DashboardViewModel
import com.pocketvibe.vm.MonthlyViewModel
import com.pocketvibe.vm.TransactionsViewModel
import com.pocketvibe.vm.WishlistViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = TransactionRepository(AppDatabase.get(this))

        setContent {
            PocketVibeTheme {
                AppNavHost(
                    dashboardVm = viewModel { DashboardViewModel(repo) },
                    transactionsVm = viewModel { TransactionsViewModel(repo) },
                    monthlyVm = viewModel { MonthlyViewModel(repo) },
                    wishlistVm = viewModel { WishlistViewModel(repo) }
                )
            }
        }
    }
}
