package dev.bltucker.spendless.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val DASHBOARD_SCREEN_ROUTE = "dashboard"

fun NavGraphBuilder.dashboardScreen(onNavigateBack: () -> Unit) {
    composable(route = DASHBOARD_SCREEN_ROUTE) {
        Box(modifier = Modifier.fillMaxSize()
        ) {
            Text("Dashboard screen")
        }
    }
}
