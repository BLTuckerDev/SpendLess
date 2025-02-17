package dev.bltucker.spendless.transactions.export

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val EXPORT_SCREEN_ROUTE = "export"

fun NavGraphBuilder.exportScreen(onNavigateBack: () -> Unit) {
    composable(route = EXPORT_SCREEN_ROUTE) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Export Screen")
        }
    }
}