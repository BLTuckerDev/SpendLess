package dev.bltucker.spendless.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val PREFERENCES_SCREEN_ROUTE = "login"


fun NavGraphBuilder.preferencesScreen(onNavigateBack: () -> Unit) {
    composable(route = PREFERENCES_SCREEN_ROUTE){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Preferences Screen")
        }
    }
}