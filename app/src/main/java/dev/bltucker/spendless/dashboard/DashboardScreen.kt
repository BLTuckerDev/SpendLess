package dev.bltucker.spendless.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class DashboardScreenNavArgs(
    val userId: Long,
)

fun NavGraphBuilder.dashboardScreen(onNavigateBack: () -> Unit) {
    composable<DashboardScreenNavArgs>() { backStackEntry ->
        val args = backStackEntry.toRoute<DashboardScreenNavArgs>()

        Box(modifier = Modifier.fillMaxSize()
        ) {
            Text("Dashboard screen: ${args.userId}")
        }
    }
}
