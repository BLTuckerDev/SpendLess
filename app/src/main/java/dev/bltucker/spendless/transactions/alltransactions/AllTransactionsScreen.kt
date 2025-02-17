package dev.bltucker.spendless.transactions.alltransactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ALL_TRANSACTIONS_SCREEN = "allTransactions"


fun NavGraphBuilder.allTransactionsScreen(onNavigateBack: () -> Unit) {
    composable(route = ALL_TRANSACTIONS_SCREEN) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "All Transactions Screen")
        }
    }
}