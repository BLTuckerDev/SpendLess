package dev.bltucker.spendless.security

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SECURITY_SCREEN_ROUTE = "security"


fun NavGraphBuilder.securityScreen(onNavigateBack: () -> Unit) {
    composable(route = SECURITY_SCREEN_ROUTE){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Security Screen")
        }
    }
}