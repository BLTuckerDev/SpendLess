package dev.bltucker.spendless.registration.createpin

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class CreatePinScreenNavArgs(
    val username: String
)


fun NavGraphBuilder.createPinScreen(onNavigateBack: () -> Unit,
                                    onNavigateToPreferences: (String, String) -> Unit) {
    composable<CreatePinScreenNavArgs>{ backStackEntry ->
        val args = backStackEntry.toRoute<CreatePinScreenNavArgs>()

        val viewModel = hiltViewModel<CreatePinScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LaunchedEffect(model.shouldNavigateToPreferences) {
            if(model.shouldNavigateToPreferences){
                val username = model.username
                val pin = model.initialPin
                onNavigateToPreferences(username, pin)
                viewModel.onHandledNavigation()
            }
        }

        BackHandler {
            viewModel.onNavigateBack()
        }


        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.username)
            onStopOrDispose {}
        }


        if (model.isConfirmingPin) {
            ConfirmPinContent(
                model = model,
                onNavigateBack = viewModel::onNavigateBack,
                onDigitEntered = viewModel::onConfirmationPinDigitEntered,
                onDeleteDigit = viewModel::onDeleteConfirmationPinDigit
            )
        } else {
            CreatePinContent(
                model = model,
                onNavigateBack = onNavigateBack,
                onDigitEntered = viewModel::onInitialPinDigitEntered,
                onDeleteDigit = viewModel::onDeleteInitialPinDigit
            )
        }
    }
}