package dev.bltucker.spendless.transactions.createtransaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val CREATE_TRANSACTIONS_SCREEN_ROUTE = "createTransactions"


fun NavGraphBuilder.createTransactionsScreen(onNavigateBack: () -> Unit){
    composable(route = CREATE_TRANSACTIONS_SCREEN_ROUTE){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Create Transactions Screen")
        }
    }
}