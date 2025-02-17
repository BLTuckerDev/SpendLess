package dev.bltucker.spendless.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val LOGIN_SCREEN_ROUTE = "login"


fun NavGraphBuilder.loginScreen(onNavigateBack: () -> Unit) {
    composable(route = LOGIN_SCREEN_ROUTE){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Login Screen")
        }
    }
}