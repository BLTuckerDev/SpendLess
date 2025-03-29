package dev.bltucker.spendless

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.spendless.authentication.AUTHENTICATION_SCREEN_NAV_ROUTE
import dev.bltucker.spendless.authentication.RE_AUTH_SUCCESS
import dev.bltucker.spendless.authentication.authenticationScreen
import dev.bltucker.spendless.authentication.createAuthenticationRoute
import dev.bltucker.spendless.dashboard.createDashboardRoute
import dev.bltucker.spendless.dashboard.dashboardScreen
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.login.loginScreen
import dev.bltucker.spendless.preferences.PreferencesScreenNavArgs
import dev.bltucker.spendless.preferences.preferencesScreen
import dev.bltucker.spendless.registration.createpin.CreatePinScreenNavArgs
import dev.bltucker.spendless.registration.createpin.createPinScreen
import dev.bltucker.spendless.registration.newuser.NEW_USER_SCREEN_ROUTE
import dev.bltucker.spendless.registration.newuser.newUserScreen
import dev.bltucker.spendless.security.SecurityScreenNavArgs
import dev.bltucker.spendless.security.securityScreen
import dev.bltucker.spendless.settings.SettingsScreenNavArgs
import dev.bltucker.spendless.settings.settingsScreen
import dev.bltucker.spendless.transactions.alltransactions.AllTransactionsScreenNavArgs
import dev.bltucker.spendless.transactions.alltransactions.allTransactionsScreen
import dev.bltucker.spendless.transactions.createtransaction.createCreateTransactionRoute
import dev.bltucker.spendless.transactions.createtransaction.createTransactionsScreen

@Composable
fun SpendLessNavigationGraph(
    navigationController: NavHostController,
    startDestination: String = LOGIN_SCREEN_ROUTE
) {
    NavHost(
        navController = navigationController,
        startDestination = startDestination
    ) {

        loginScreen(
            onNavigateToNewUser = {
                navigationController.navigate(NEW_USER_SCREEN_ROUTE)
            },
            onLoginSuccess = { userId ->
                navigationController.navigate(createDashboardRoute(userId)) {
                    popUpTo(LOGIN_SCREEN_ROUTE) {
                        inclusive = true
                    }
                }
            },
        )

        preferencesScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },

            onNavigateBackToPinCreate = { username ->
                navigationController.popBackStack(
                    route = CreatePinScreenNavArgs(username),
                    inclusive = false
                )
            },

            onPromptForPin = {
                navigationController.navigate(createAuthenticationRoute(null))
            },

            onNavigateToDashboard = { userId ->
                navigationController.navigate(createDashboardRoute(userId)) {
                    popUpTo(LOGIN_SCREEN_ROUTE) {
                        inclusive = false
                    }
                }
            }
        )

        createPinScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToPreferences = { username, pin ->
                navigationController.navigate(
                    PreferencesScreenNavArgs(
                        userId = null,
                        username = username,
                        pin = pin
                    )
                )
            }
        )

        newUserScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToCreatePin = { username ->
                navigationController.navigate(CreatePinScreenNavArgs(username))
            },
            onNavigateToLogin = {
                navigationController.navigate(LOGIN_SCREEN_ROUTE)
            }
        )

        settingsScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToPreferences = { userId ->
                navigationController.navigate(PreferencesScreenNavArgs(userId))
            },
            onNavigateToSecurity = { userId ->
                navigationController.navigate(SecurityScreenNavArgs(userId))
            },
            onNavigateToLogout = {
                navigationController.navigate(LOGIN_SCREEN_ROUTE) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )

        securityScreen(
            onPromptForPin = {
                navigationController.navigate(createAuthenticationRoute(null))
            },
            onNavigateBack = {
                navigationController.popBackStack()
            })

        ///TODO needs fab for creating
        //TODO needs content for the stuff above the bottomsheet
        dashboardScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToCreateTransaction = { userId ->
                navigationController.navigate(createCreateTransactionRoute(userId))
            },
            onSettingsClick = { userId ->
                navigationController.navigate(SettingsScreenNavArgs(userId))
            },
            onShowAllTransactionsClick = { userId ->
                navigationController.navigate(AllTransactionsScreenNavArgs(userId))
            },
            onPromptForPin = {
                navigationController.navigate(createAuthenticationRoute(null))
            },
            onFallBackToLogin = {
                navigationController.navigate(LOGIN_SCREEN_ROUTE) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )

        //TODO needs a fab for creating
        allTransactionsScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onPromptForPin = {
                navigationController.navigate(createAuthenticationRoute(null))
            },
        )

        authenticationScreen(
            onNavigateBack = {
                Log.d("NavDebug", "Popping Back")
                navigationController.previousBackStackEntry?.savedStateHandle?.set(
                    RE_AUTH_SUCCESS,
                    true
                )
                navigationController.popBackStack()
            },
            onLogoutClicked = {
                navigationController.navigate(LOGIN_SCREEN_ROUTE) {
                    popUpTo(navigationController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onNavigateToIntendedDestination = { destinationRoute ->
                Log.d("NavDebug", "Destination: $destinationRoute")
                navigationController.previousBackStackEntry?.savedStateHandle?.set(
                    RE_AUTH_SUCCESS,
                    true
                )

                navigationController.navigate(destinationRoute) {
                    popUpTo(AUTHENTICATION_SCREEN_NAV_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            })

        // ---------------- TODO ----------------------------


        createTransactionsScreen(
            onNavigateBack = { userId ->
                navigationController.navigate(createDashboardRoute(userId)){
                    popUpTo(createCreateTransactionRoute(userId)) {
                        inclusive = true
                    }
                    launchSingleTop = true

                }
            },
            onPromptForPin = {
                navigationController.navigate(createAuthenticationRoute(null))
            },
        )


    }
}