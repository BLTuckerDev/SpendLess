package dev.bltucker.spendless

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.spendless.authentication.authenticationScreen
import dev.bltucker.spendless.dashboard.DASHBOARD_SCREEN_ROUTE
import dev.bltucker.spendless.dashboard.dashboardScreen
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.login.loginScreen
import dev.bltucker.spendless.preferences.PREFERENCES_SCREEN_ROUTE
import dev.bltucker.spendless.preferences.preferencesScreen
import dev.bltucker.spendless.registration.createpin.createPinScreen
import dev.bltucker.spendless.registration.newuser.NEW_USER_SCREEN_ROUTE
import dev.bltucker.spendless.registration.newuser.newUserScreen
import dev.bltucker.spendless.security.SECURITY_SCREEN_ROUTE
import dev.bltucker.spendless.security.securityScreen
import dev.bltucker.spendless.settings.SETTINGS_SCREEN_ROUTE
import dev.bltucker.spendless.settings.settingsScreen
import dev.bltucker.spendless.transactions.alltransactions.allTransactionsScreen
import dev.bltucker.spendless.transactions.createtransaction.createTransactionsScreen
import dev.bltucker.spendless.transactions.export.exportScreen

@Composable
fun SpendLessNavigationGraph(navigationController: NavHostController) {
    NavHost(
        navController = navigationController,
        startDestination = SETTINGS_SCREEN_ROUTE
    ) {


        loginScreen(
            onNavigateToNewUser = {
                navigationController.navigate(NEW_USER_SCREEN_ROUTE)
            },
            onLoginSuccess = {
                navigationController.navigate(DASHBOARD_SCREEN_ROUTE)
            },)

        authenticationScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        dashboardScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        preferencesScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        createPinScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        newUserScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        securityScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

        settingsScreen(
            onNavigateBack = {
                navigationController.popBackStack()
            },
            onNavigateToPreferences = {
                navigationController.navigate(PREFERENCES_SCREEN_ROUTE)
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