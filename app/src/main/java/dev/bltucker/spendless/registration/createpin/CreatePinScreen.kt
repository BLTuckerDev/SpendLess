package dev.bltucker.spendless.registration.createpin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

const val CREATE_PIN_SCREEN_ROUTE = "createPin"

@Serializable
data class CreatePinScreenNavArgs(
    val username: String
)


fun NavGraphBuilder.createPinScreen(onNavigateBack: () -> Unit) {
    composable<CreatePinScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<CreatePinScreenNavArgs>()
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Create Pin Screen: ${args.username}")
        }
    }
}