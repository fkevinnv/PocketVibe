package com.pocketvibe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pocketvibe.data.local.TransactionType
import com.pocketvibe.ui.screens.AddTransactionSheet
import com.pocketvibe.ui.screens.DashboardScreen
import com.pocketvibe.ui.screens.HistoryScreen
import com.pocketvibe.ui.screens.MonthlySummaryScreen
import com.pocketvibe.ui.screens.ScannerScreen
import com.pocketvibe.ui.screens.WishlistScreen
import com.pocketvibe.vm.DashboardViewModel
import com.pocketvibe.vm.MonthlyViewModel
import com.pocketvibe.vm.TransactionsViewModel
import com.pocketvibe.vm.WishlistViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

object Routes {
    const val DASHBOARD = "dashboard"
    const val SCANNER = "scanner"
    const val MONTHLY = "monthly"
    const val WISHLIST = "wishlist"
    const val HISTORY = "history"
}

@Composable
fun AppNavHost(
    dashboardVm: DashboardViewModel,
    transactionsVm: TransactionsViewModel,
    monthlyVm: MonthlyViewModel,
    wishlistVm: WishlistViewModel
) {
    val nav = rememberNavController()
    val txState by transactionsVm.uiState.collectAsStateWithLifecycle()
    var sheetType by remember { mutableStateOf<TransactionType?>(null) }

    NavHost(navController = nav, startDestination = Routes.DASHBOARD) {

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                state = dashboardVm.uiState.collectAsStateWithLifecycle().value,
                recent = txState.recent,
                onAddIncome = { sheetType = TransactionType.INCOME },
                onAddExpense = { sheetType = TransactionType.EXPENSE },
                onScan = { nav.navigate(Routes.SCANNER) },
                onSeeMonthly = { nav.navigate(Routes.MONTHLY) },
                onSeeWishlist = { nav.navigate(Routes.WISHLIST) },
                onSeeHistory = { nav.navigate(Routes.HISTORY) }
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onScanned = { amount ->
                    transactionsVm.add(
                        amount = amount,
                        type = TransactionType.EXPENSE,
                        category = com.pocketvibe.data.local.Category.SHOPPING,
                        note = "Ticket escaneado",
                        source = com.pocketvibe.data.local.TxSource.SCAN
                    )
                    nav.popBackStack()
                },
                onClose = { nav.popBackStack() }
            )
        }

        composable(Routes.MONTHLY) { MonthlySummaryScreen(monthlyVm) }
        composable(Routes.WISHLIST) { WishlistScreen(wishlistVm) }
        composable(Routes.HISTORY) { HistoryScreen(transactionsVm) }
    }

    // Sheet inferior para añadir ingreso / gasto
    sheetType?.let { type ->
        AddTransactionSheet(
            type = type,
            onSave = { amount, category, note ->
                transactionsVm.add(amount, type, category, note)
                sheetType = null
            },
            onDismiss = { sheetType = null }
        )
    }
}
