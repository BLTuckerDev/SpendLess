package dev.bltucker.spendless.dashboard

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import dev.bltucker.spendless.R
import dev.bltucker.spendless.authentication.RE_AUTH_SUCCESS
import dev.bltucker.spendless.common.composables.ErrorScreen
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.composables.LocalTransactionFormatter
import dev.bltucker.spendless.common.composables.TransactionByDayItem
import dev.bltucker.spendless.common.composables.TransactionFormatter
import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.SpendLessUser
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.room.UserPreferences
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.dashboard.composables.AccountBalance
import dev.bltucker.spendless.transactions.export.composables.ExportBottomSheetModal
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


data class DashboardActions(
    val onSettingsClick: () -> Unit,
    val onTransactionClicked: (Long) -> Unit,
    val onShowAllTransactionsClick: () -> Unit,
    val onExportClick: () -> Unit,
    val onDismissExportBottomSheet: () -> Unit = {},
    val onPromptForPin: () -> Unit = {}
)

fun createDashboardRoute(userId: Long): String {
    return "dashboard/$userId"
}

fun NavGraphBuilder.dashboardScreen(
    onFallBackToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    onSettingsClick: (Long) -> Unit,
    onShowAllTransactionsClick: (Long) -> Unit,
    onPromptForPin: () -> Unit,

    ) {
    composable(
        route = "dashboard/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.LongType })
    ) { backStackEntry ->
        val coroutineScope = rememberCoroutineScope()
        val userId = backStackEntry.arguments?.getLong("userId")
            ?: run {
                LaunchedEffect(Unit) {
                    onFallBackToLogin()
                }
                -1L
            }

        val savedStateHandle = backStackEntry.savedStateHandle
        val viewModel = hiltViewModel<DashboardScreenViewModel>()

        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        Log.d(
            "DashboardDebug",
            "reauth action: ${model.reAuthAction}, should reauth: ${model.shouldReauthenticate}"
        )

        LaunchedEffect(savedStateHandle) {
            savedStateHandle.getLiveData<Boolean>(RE_AUTH_SUCCESS)
                .observe(backStackEntry) { success ->
                    if (success) {
                        savedStateHandle.remove<Boolean>(RE_AUTH_SUCCESS)
                        val reAuthAction = model.reAuthAction

                        when (reAuthAction) {
                            ReAuthAction.SHOW_ALL -> onShowAllTransactionsClick(userId)
                            ReAuthAction.FAB -> { /* HANDLE FAB */
                            }

                            ReAuthAction.SETTINGS -> onSettingsClick(userId)
                            null -> {}
                        }

                        viewModel.onConsumeReAuthAction()
                    }
                }
        }

        LifecycleStartEffect(Unit) {
            viewModel.onStart(userId)

            onStopOrDispose { }
        }

        BackHandler {
            onNavigateBack()
        }

        LaunchedEffect(model.shouldReauthenticate) {
            if(model.shouldReauthenticate){
                onPromptForPin()
                viewModel.onClearShouldReauthenticate()
            }
        }

        val dashboardActions = DashboardActions(
            onSettingsClick = {
                coroutineScope.launch {
                    val needsReAuth = viewModel.onCheckForReAuth(ReAuthAction.SETTINGS)
                    if (!needsReAuth) {
                        onSettingsClick(userId)
                    }
                }
            },
            onTransactionClicked = viewModel::onTransactionClicked,
            onExportClick = viewModel::onShowExportBottomSheet,
            onShowAllTransactionsClick = {
                coroutineScope.launch {
                    val needsReAuth = viewModel.onCheckForReAuth(ReAuthAction.SHOW_ALL)
                    if (!needsReAuth) {
                        onShowAllTransactionsClick(userId)
                    }
                }

            },
            onDismissExportBottomSheet = viewModel::onHideExportBottomSheet,
            onPromptForPin = onPromptForPin
        )

        when {
            model.isLoading -> LoadingSpinner()
            model.isError -> ErrorScreen()
            else -> {
                CompositionLocalProvider(
                    LocalTransactionFormatter provides TransactionFormatter(
                        currencySymbol = model.userPreferences?.currencySymbol ?: "$",
                        thousandsSeparator = model.userPreferences?.thousandsSeparator ?: ",",
                        decimalSeparator = model.userPreferences?.decimalSeparator ?: ".",
                        useBracketsForExpense = model.userPreferences?.useBracketsForExpense
                            ?: false
                    )
                ) {
                    DashboardScaffold(
                        modifier = Modifier.fillMaxSize(),
                        dashboardActions = dashboardActions,
                        model = model,
                        backStackEntry = backStackEntry
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScaffold(
    modifier: Modifier = Modifier,
    dashboardActions: DashboardActions,
    model: DashboardScreenModel,
    backStackEntry: NavBackStackEntry?,

    ) {

    if (model.showExportBottomSheet) {
        model.user?.id?.let { userId ->
            ExportBottomSheetModal(
                userId = userId,
                backStackEntry = backStackEntry,
                onPromptForPin = { dashboardActions.onPromptForPin() },
                onDismiss = { dashboardActions.onDismissExportBottomSheet() }
            )
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        sheetContent = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Latest Transactions",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = { dashboardActions.onShowAllTransactionsClick() }) {
                        Text("Show all")
                    }
                }

                LazyColumn {
                    items(model.transactionsGroupedByDate) {
                        TransactionByDayItem(
                            modifier = Modifier.fillMaxWidth(),
                            formattedDate = it.dateLabel,
                            transactions = it.transactions,
                            selectedTransactionId = model.clickedTransactionId,
                            onTransactionClicked = dashboardActions.onTransactionClicked,
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        sheetPeekHeight = 400.dp,
        sheetContainerColor = Color(0xFFFEF7FF),
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = model.user?.username ?: "",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (model.transactions.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    color = Color(0x1FFFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            onClick = { dashboardActions.onExportClick() }) {
                            Icon(
                                painter = painterResource(R.drawable.export),
                                contentDescription = "Export",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    IconButton(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(
                                color = Color(0x1FFFFFFF),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        onClick = { dashboardActions.onSettingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {

            AccountBalance(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp, horizontal = 72.dp),
                accountBalance = model.formattedAccountBalance()
            )
        }
    }
}


@Preview
@Composable
fun DashboardScaffoldPreview() {

    val transactionFormatter = TransactionFormatter(
        currencySymbol = "$",
        thousandsSeparator = ",",
        decimalSeparator = ".",
        useBracketsForExpense = true
    )

    val sampleTransactions = listOf(
        TransactionData(
            id = 1,
            userId = 1,
            amount = 158999, // $1,589.99
            isExpense = true,
            name = "Monthly Rent",
            category = TransactionCategory.HOME,
            note = "Rent payment for February",
            createdAt = LocalDateTime.now(),
            recurringFrequency = RecurringFrequency.MONTHLY
        ),
        TransactionData(
            id = 2,
            userId = 1,
            amount = 12550, // $125.50
            isExpense = true,
            name = "Grocery Shopping",
            category = TransactionCategory.FOOD_AND_GROCERIES,
            note = null,
            createdAt = LocalDateTime.now().minusHours(2),
            recurringFrequency = RecurringFrequency.DOES_NOT_REPEAT
        ),
        TransactionData(
            id = 3,
            userId = 1,
            amount = 500000, // $5,000.00
            isExpense = false,
            name = "Salary Deposit",
            category = null,
            note = "Monthly salary payment",
            createdAt = LocalDateTime.now().minusHours(4),
            recurringFrequency = RecurringFrequency.MONTHLY
        ),
        TransactionData(
            id = 4,
            userId = 1,
            amount = 4999, // $49.99
            isExpense = true,
            name = "Netflix Subscription",
            category = TransactionCategory.ENTERTAINMENT,
            note = "Monthly streaming service",
            createdAt = LocalDateTime.now().minusHours(6),
            recurringFrequency = RecurringFrequency.MONTHLY
        ),
        TransactionData(
            id = 5,
            userId = 1,
            amount = 8500, // $85.00
            isExpense = true,
            name = "Gas Station",
            category = TransactionCategory.TRANSPORTATION,
            note = null,
            createdAt = LocalDateTime.now().minusHours(8),
            recurringFrequency = RecurringFrequency.DOES_NOT_REPEAT
        )
    )
    SpendLessTheme {


        val model = DashboardScreenModel(
            user = SpendLessUser(username = "test", pinHash = "", pinSalt = ""),
            transactions = sampleTransactions,
            userPreferences = UserPreferences(0, false, "$", ".", ","),
            clickedTransactionId = 1,
            isLoading = false,
            isError = false
        )

        val actions = DashboardActions(
            onSettingsClick = {},
            onTransactionClicked = {},
            onExportClick = {},
            onShowAllTransactionsClick = {},
        )

        CompositionLocalProvider(
            LocalTransactionFormatter provides transactionFormatter
        ) {
            DashboardScaffold(
                modifier = Modifier.fillMaxSize(),
                dashboardActions = actions,
                model = model,
                backStackEntry = null
            )
        }
    }
}