package dev.bltucker.spendless.authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.composables.ErrorBanner
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.composables.PinDots
import dev.bltucker.spendless.common.composables.PinKeypad
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.common.theme.SurfaceContainerLowest

const val DESTINATION_ROUTE_ARG = "destinationRoute"
const val AUTHENTICATION_SCREEN_ROUTE = "authentication"

const val AUTHENTICATION_SCREEN_NAV_ROUTE =
    "$AUTHENTICATION_SCREEN_ROUTE?$DESTINATION_ROUTE_ARG={$DESTINATION_ROUTE_ARG}"


fun createAuthenticationRoute(destinationRoute: String?): String {
    return if (destinationRoute == null) {
        AUTHENTICATION_SCREEN_ROUTE
    } else {
        "$AUTHENTICATION_SCREEN_ROUTE?$DESTINATION_ROUTE_ARG=$destinationRoute"
    }
}


fun NavGraphBuilder.authenticationScreen(onNavigateBack: () -> Unit,
                                         onNavigateToIntendedDestination: (String) -> Unit) {
    composable( route = AUTHENTICATION_SCREEN_NAV_ROUTE,
        arguments = listOf(navArgument(DESTINATION_ROUTE_ARG) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })) { backStackEntry ->
        val viewModel = hiltViewModel<AuthenticationScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val destinationRoute = backStackEntry.arguments?.getString(DESTINATION_ROUTE_ARG)


        LaunchedEffect(Unit) {
            viewModel.onStart()
        }

        LaunchedEffect(model.authenticationSuccessful, destinationRoute) {
            if (model.authenticationSuccessful) {
                if (destinationRoute != null) {
                    onNavigateToIntendedDestination(destinationRoute)
                } else {
                    onNavigateBack()
                }
            }
        }

                AuthenticationScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onPinDigitEntered = viewModel::onPinDigitEntered,
            onDeletePinDigit = viewModel::onDeletePinDigit
        )
    }
}

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    model: AuthenticationScreenModel,
    onPinDigitEntered: (String) -> Unit,
    onDeletePinDigit: () -> Unit
) {
    Box(
        modifier = modifier.systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = SurfaceContainerLowest
        ) {
            if (model.isLoading && !model.isLocked) {
                LoadingSpinner()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(72.dp))

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(color = Primary, shape = RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.wallet_money),
                            contentDescription = "App Logo"
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))


                    // --- Normal/Error State UI ---
                    Text(
                        text = if (model.username != null) "Hello, ${model.username}!" else "Hello!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Enter your PIN",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    PinDots(pinLength = model.pin.length)

                    Spacer(modifier = Modifier.height(32.dp))

                    PinKeypad(
                        enabled = !model.isLocked,
                        onDigitEntered = onPinDigitEntered,
                        onDeleteClick = onDeletePinDigit
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = model.isError, // Show whenever isError is true
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ErrorBanner(message = "Wrong PIN")
        }
    }
}

// --- Previews (Add or Update) ---

@Preview(showBackground = true, name = "Authentication Screen - Error")
@Composable
fun AuthenticationScreenErrorPreviewUpdated() {
    SpendLessTheme {
        AuthenticationScreen(
            model = AuthenticationScreenModel(
                userId = 1L,
                username = "Jane Doe",
                pin = "", // PIN resets on error
                isLoading = false,
                isError = true, // Error is true
                errorMessage = "Incorrect PIN. Attempts: 1/3", // ViewModel might still hold this
                failedAttempts = 1,
                showFailedAttemptsMessage = true, // ViewModel might still set this
                isLocked = false, // Not locked yet
                lockoutTimeRemainingSeconds = 0,
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {},
            onDeletePinDigit = {}
        )
    }
}

@Preview(showBackground = true, name = "Authentication Screen - Locked")
@Composable
fun AuthenticationScreenLockedPreviewUpdated() {
    SpendLessTheme {
        AuthenticationScreen(
            model = AuthenticationScreenModel(
                userId = 1L,
                username = "Jane Doe", // Username might still be present in model
                pin = "", // PIN resets
                isLoading = false, // Not loading during lockout typically
                isError = true, // Still an error state
                errorMessage = "Account locked. Try again in 30s", // ViewModel might hold this
                failedAttempts = 3,
                showFailedAttemptsMessage = true,
                isLocked = true, // Locked is true
                lockoutTimeRemainingSeconds = 30, // Example remaining time
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {},
            onDeletePinDigit = {}
        )
    }
}

@Preview(showBackground = true, name = "Authentication Screen")
@Composable
fun AuthenticationScreenPreviewUpdated() {
    SpendLessTheme { // Wrap in your app's theme
        AuthenticationScreen(
            model = AuthenticationScreenModel(
                userId = 1L,
                username = "Jane Doe", // Sample username
                pin = "12",            // Sample PIN state (e.g., 2 digits entered)
                isLoading = false,
                isError = false,
                errorMessage = null,
                failedAttempts = 0,
                isLocked = false,
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {}, // No-op lambda for preview
            onDeletePinDigit = {}   // No-op lambda for preview
        )
    }
}

