package dev.bltucker.spendless.dashboard

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import dev.bltucker.spendless.dashboard.composables.LargestTransactionItem
import dev.bltucker.spendless.dashboard.composables.MostPopularCategory
import dev.bltucker.spendless.dashboard.composables.NoLargestTransactionItem
import dev.bltucker.spendless.dashboard.composables.PreviousWeekTransactionTotal
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
    val onPromptForPin: () -> Unit = {},
    val onCreateTransactionClick: () -> Unit = {},
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
    onNavigateToCreateTransaction: (Long) -> Unit,
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
                            ReAuthAction.FAB -> {
                                onNavigateToCreateTransaction(userId)
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
            onPromptForPin = onPromptForPin,
            onCreateTransactionClick = {
                coroutineScope.launch {
                    val needsReAuth = viewModel.onCheckForReAuth(ReAuthAction.FAB)
                    if (!needsReAuth) {
                        onNavigateToCreateTransaction(userId)
                    }
                }
            }
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

    Box(modifier = modifier,){
        BottomSheetScaffold(
            modifier = Modifier,
            sheetContent = {

                if(model.transactions.isEmpty()){
                    NoTransactionsView(Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                } else {
                    TransactionsListView(dashboardActions = dashboardActions,model = model)
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
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                AccountBalance(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 72.dp),
                    accountBalance = model.formattedAccountBalance()
                )

                Spacer(modifier.weight(1F))


                if(model.mostPopularCategory != null){
                    MostPopularCategory(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        mostPopularCategory = model.mostPopularCategory
                    )
                }


                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(IntrinsicSize.Min),
                ){
                    val transactionFormatter = LocalTransactionFormatter.current

                    if(model.largestTransaction != null){
                        LargestTransactionItem(
                            modifier = Modifier.weight(2F).fillMaxHeight(),
                            transactionTitle = model.largestTransaction.name,
                            formattedTransactionAmount =  transactionFormatter.formatAmount(model.largestTransaction.amount, model.largestTransaction.isExpense),
                            formattedTransactionDate = model.formatTransactionDate(model.largestTransaction.createdAt)

                        )

                    } else {
                        NoLargestTransactionItem(modifier = Modifier.weight(2F).fillMaxHeight())
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Log.d("DashboardDebug", "Previous Week Total: ${model.previousWeekTotalSpent}")

                    PreviousWeekTransactionTotal(
                        modifier = Modifier.weight(1F).fillMaxHeight(),
                        formattedTotal = transactionFormatter.formatAmount(model.previousWeekTotalSpent, true))

                }
            }
        }

        FloatingActionButton(
            onClick = { dashboardActions.onCreateTransactionClick() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd) // Position the FAB
                .padding(16.dp)
                .padding(NavigationBarDefaults.windowInsets.asPaddingValues())

        ) {
            Icon(Icons.Filled.Add, "Create Transaction")
        }
    }
}

@Composable
private fun TransactionsListView(
    modifier: Modifier = Modifier,
    dashboardActions: DashboardActions,
    model: DashboardScreenModel
) {
    Column(
        modifier = modifier
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
}

@Composable
private fun NoTransactionsView(modifier: Modifier = Modifier){
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        Image(
            modifier = Modifier.size(122.dp, 114.dp),
            painter = painterResource(id = R.drawable.no_money), contentDescription = "No Transactions")

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "No transactions to show", fontSize = 20.sp, textAlign = TextAlign.Center)
    }
}

@Preview()
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
            id = 235,
            userId = 1,
            amount = 12550, // $125.50
            isExpense = true,
            name = "Grocery Shopping",
            category = TransactionCategory.FOOD_AND_GROCERIES,
            note = null,
            createdAt = LocalDateTime.now().minusDays(8),
            recurringFrequency = RecurringFrequency.DOES_NOT_REPEAT
        ),
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