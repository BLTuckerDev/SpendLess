package dev.bltucker.spendless.preferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable


@Serializable
data class PreferencesScreenNavArgs(
    val userId: Long?,
    val username: String? = null,
    val pin: String? = null,
)

fun NavGraphBuilder.preferencesScreen(onNavigateBack: () -> Unit) {
    composable<PreferencesScreenNavArgs> { backStackEntry ->
        val args = backStackEntry.toRoute<PreferencesScreenNavArgs>()

        //if we have a user id then the user has been created
        //if we dont, then we must have username and pin
        //if we are missing data -> error state


        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Preferences Screen: ${args.userId}, ${args.username}, ${args.pin}")
        }
    }
}