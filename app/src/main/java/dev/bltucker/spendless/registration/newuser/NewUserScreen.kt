package dev.bltucker.spendless.registration.newuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val NEW_USER_SCREEN_ROUTE = "newUser"

fun NavGraphBuilder.newUserScreen(onNavigateBack: () -> Unit) {
    composable(NEW_USER_SCREEN_ROUTE) {
        Box(modifier = Modifier.fillMaxWidth()){
            Text(text = "New User Screen")
        }
    }
}
