package dev.bltucker.spendless.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


const val SETTINGS_SCREEN_ROUTE = "settings"

fun NavGraphBuilder.settingsScreen(onNavigateBack: () -> Unit) {
    composable(route = SETTINGS_SCREEN_ROUTE) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Settings Screen")
        }
    }
}