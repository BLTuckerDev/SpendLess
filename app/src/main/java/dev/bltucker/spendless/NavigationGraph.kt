package dev.bltucker.spendless

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.spendless.authentication.authenticationScreen
import dev.bltucker.spendless.dashboard.DASHBOARD_SCREEN_ROUTE
import dev.bltucker.spendless.dashboard.dashboardScreen
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.login.loginScreen
import dev.bltucker.spendless.preferences.PreferencesScreenNavArgs
import dev.bltucker.spendless.preferences.preferencesScreen
import dev.bltucker.spendless.registration.createpin.CreatePinScreenNavArgs
import dev.bltucker.spendless.registration.createpin.createPinScreen
import dev.bltucker.spendless.registration.newuser.NEW_USER_SCREEN_ROUTE
import dev.bltucker.spendless.registration.newuser.newUserScreen
import dev.bltucker.spendless.security.SECURITY_SCREEN_ROUTE
import dev.bltucker.spendless.security.securityScreen
import dev.bltucker.spendless.settings.settingsScreen
import dev.bltucker.spendless.transactions.alltransactions.allTransactionsScreen
import dev.bltucker.spendless.transactions.createtransaction.createTransactionsScreen
import dev.bltucker.spendless.transactions.export.exportScreen

@Composable
fun SpendLessNavigationGraph(navigationController: NavHostController) {
    NavHost(
        navController = navigationController,
        startDestination = LOGIN_SCREEN_ROUTE
    ) {


        loginScreen(
            onNavigateToNewUser = {
                navigationController.navigate(NEW_USER_SCREEN_ROUTE)
            },
            onLoginSuccess = {
                navigationController.navigate(DASHBOARD_SCREEN_ROUTE) {
                    popUpTo(LOGIN_SCREEN_ROUTE) {
                        inclusive = true
                    }
                }
            },
        )

        authenticationScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        dashboardScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        preferencesScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        createPinScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToPreferences = { username, pin ->
                navigationController.navigate(PreferencesScreenNavArgs(userId = null, username = username, pin = pin))
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

        securityScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        settingsScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToPreferences = { userId ->
                navigationController.navigate(PreferencesScreenNavArgs(userId))
            },
            onNavigateToSecurity = {
                navigationController.navigate(SECURITY_SCREEN_ROUTE)
            },
            onNavigateToLogout = {
                navigationController.navigate(LOGIN_SCREEN_ROUTE)
            }
        )

        allTransactionsScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        createTransactionsScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        exportScreen(onNavigateBack = {
            navigationController.popBackStack()
        })
    }
}