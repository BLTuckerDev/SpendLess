package dev.bltucker.spendless

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.spendless.authentication.authenticationScreen
import dev.bltucker.spendless.dashboard.dashboardScreen
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.login.loginScreen
import dev.bltucker.spendless.preferences.preferencesScreen
import dev.bltucker.spendless.registration.createpin.createPinScreen
import dev.bltucker.spendless.registration.newuser.newUserScreen
import dev.bltucker.spendless.security.securityScreen
import dev.bltucker.spendless.settings.settingsScreen
import dev.bltucker.spendless.transactions.alltransactions.allTransactionsScreen
import dev.bltucker.spendless.transactions.createtransaction.createTransactionsScreen
import dev.bltucker.spendless.transactions.export.exportScreen

@Composable
fun SpendLessNavigationGraph(navigationController: NavHostController) {
    NavHost(navController = navigationController,
        startDestination = LOGIN_SCREEN_ROUTE){


        loginScreen(onNavigateBack = {
            navigationController.popBackStack()
        })
        
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

        settingsScreen(onNavigateBack = {
            navigationController.popBackStack()
        })

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