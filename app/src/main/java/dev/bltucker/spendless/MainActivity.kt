package dev.bltucker.spendless

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.bltucker.spendless.authentication.createAuthenticationRoute
import dev.bltucker.spendless.common.UserSessionManager
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.dashboard.createDashboardRoute
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.transactions.createtransaction.CREATE_TRANSACTIONS_SCREEN_ROUTE
import dev.bltucker.spendless.widget.CreateTransactionWidget.Companion.ACTION_OPEN_CREATE_TRANSACTION
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userSessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = determineStartDestination(intent)
        Log.d("NavDebug", "Start Destination: $startDestination")
        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            var isInitialResume by remember { mutableStateOf(true) }

            LifecycleResumeEffect(Unit) {

                if(isInitialResume){
                    isInitialResume = false
                } else {
                    scope.launch {
                        Log.d("NavDebug", "Resumed - checking session")
                        val needsToReauth = userSessionManager.needsReauthentication()
                        if(needsToReauth){
                            navController.navigate(createAuthenticationRoute(null))
                        }
                    }
                }


                onPauseOrDispose {  }
            }


            SpendLessTheme {
                SpendLessNavigationGraph(
                    navigationController = navController,
                    startDestination = startDestination)
            }
        }
    }

    private fun determineStartDestination(intent: Intent): String {
        return when (intent.action) {
            ACTION_OPEN_CREATE_TRANSACTION -> {
                val lastLoggedInUserId = userRepository.getLastLoggedInUser()
                if (lastLoggedInUserId != null) {
                    "$CREATE_TRANSACTIONS_SCREEN_ROUTE/${lastLoggedInUserId}"
                } else {
                    LOGIN_SCREEN_ROUTE
                }
            }
            else -> {
                val userId = userSessionManager.getLastLoggedInUser()
                val needsReAuth = runBlocking { userSessionManager.needsReauthentication() }

                if(needsReAuth && userId != null){
                    createAuthenticationRoute(createDashboardRoute(userId))
                } else {
                    LOGIN_SCREEN_ROUTE
                }
            }
        }
    }
}