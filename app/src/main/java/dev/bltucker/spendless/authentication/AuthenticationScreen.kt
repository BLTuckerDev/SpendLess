package dev.bltucker.spendless.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val AUTHENTICATION_SCREEN_ROUTE = "authentication"


fun NavGraphBuilder.authenticationScreen(onNavigateBack: () -> Unit) {
    composable(route = AUTHENTICATION_SCREEN_ROUTE){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Authentication Screen")
        }
    }
}