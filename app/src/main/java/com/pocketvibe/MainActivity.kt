package com.pocketvibe

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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

        // Con targetSdk 35 Android dibuja la app bajo las barras del sistema:
        // tema oscuro fijo + padding de insets para que nada quede tapado.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val repo = TransactionRepository(AppDatabase.get(this))

        setContent {
            PocketVibeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(Modifier.fillMaxSize().systemBarsPadding()) {
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
    }
}
