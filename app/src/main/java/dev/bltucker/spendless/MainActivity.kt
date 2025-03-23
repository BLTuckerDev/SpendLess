package dev.bltucker.spendless

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.login.LOGIN_SCREEN_ROUTE
import dev.bltucker.spendless.transactions.createtransaction.CREATE_TRANSACTIONS_SCREEN_ROUTE
import dev.bltucker.spendless.widget.CreateTransactionWidget.Companion.ACTION_OPEN_CREATE_TRANSACTION
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = determineStartDestination(intent)

        setContent {
            val navController = rememberNavController()
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
            else -> LOGIN_SCREEN_ROUTE
        }
    }
}