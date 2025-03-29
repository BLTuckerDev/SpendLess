package dev.bltucker.spendless.authentication

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

const val RE_AUTH_SUCCESS = "reAuthSuccess"

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


fun NavGraphBuilder.authenticationScreen(
    onNavigateBack: () -> Unit,
    onLogoutClicked: () -> Unit,
    onNavigateToIntendedDestination: (String) -> Unit
) {
    composable(
        route = AUTHENTICATION_SCREEN_NAV_ROUTE,
        arguments = listOf(navArgument(DESTINATION_ROUTE_ARG) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) { backStackEntry ->
        val viewModel = hiltViewModel<AuthenticationScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val destinationRoute = backStackEntry.arguments?.getString(DESTINATION_ROUTE_ARG)


        LaunchedEffect(Unit) {
            viewModel.onStart()
        }

        LaunchedEffect(model.authenticationSuccessful, destinationRoute) {
            Log.d("NavDebug", "Launched Effect")
            if (model.authenticationSuccessful) {
                Log.d("NavDebug", "Launched Effect auth successful")
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
            onDeletePinDigit = viewModel::onDeletePinDigit,
            onLogoutClicked = {
                viewModel.onClearSession()
                onLogoutClicked()
            }
        )
    }
}

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    model: AuthenticationScreenModel,
    onLogoutClicked: () -> Unit,
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
                Box(modifier = Modifier.fillMaxSize()) {
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


                    Button(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp)
                            .align(Alignment.TopEnd),
                        onClick = { onLogoutClicked() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x14A40019),
                            contentColor = Color(0xFFFF3B30)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color(0xFFFF3B30)
                        )
                    }
                }

            }
        }

        AnimatedVisibility(
            visible = model.isError,
            enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ErrorBanner(message = "Wrong PIN")
        }
    }
}

@Preview(showBackground = true, name = "Authentication Screen - Error")
@Composable
fun AuthenticationScreenErrorPreviewUpdated() {
    SpendLessTheme {
        AuthenticationScreen(
            model = AuthenticationScreenModel(
                userId = 1L,
                username = "Jane Doe",
                pin = "",
                isLoading = false,
                isError = true,
                errorMessage = "Incorrect PIN. Attempts: 1/3",
                failedAttempts = 1,
                showFailedAttemptsMessage = true,
                isLocked = false,
                lockoutTimeRemainingSeconds = 0,
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {},
            onDeletePinDigit = {},
            onLogoutClicked = {},
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
                username = "Jane Doe",
                pin = "",
                isLoading = false,
                isError = true,
                errorMessage = "Account locked. Try again in 30s",
                failedAttempts = 3,
                showFailedAttemptsMessage = true,
                isLocked = true,
                lockoutTimeRemainingSeconds = 30,
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {},
            onDeletePinDigit = {},
            onLogoutClicked = {},
        )
    }
}

@Preview(showBackground = true, name = "Authentication Screen")
@Composable
fun AuthenticationScreenPreviewUpdated() {
    SpendLessTheme {
        AuthenticationScreen(
            model = AuthenticationScreenModel(
                userId = 1L,
                username = "Jane Doe",
                pin = "12",
                isLoading = false,
                isError = false,
                errorMessage = null,
                failedAttempts = 0,
                isLocked = false,
                authenticationSuccessful = false
            ),
            onPinDigitEntered = {},
            onDeletePinDigit = {},
            onLogoutClicked = {},
        )
    }
}

