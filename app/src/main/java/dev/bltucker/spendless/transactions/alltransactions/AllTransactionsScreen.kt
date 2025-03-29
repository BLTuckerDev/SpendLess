package dev.bltucker.spendless.transactions.alltransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.bltucker.spendless.R
import dev.bltucker.spendless.common.composables.ErrorScreen
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.composables.LocalTransactionFormatter
import dev.bltucker.spendless.common.composables.TransactionByDayItem
import dev.bltucker.spendless.common.composables.TransactionFormatter
import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.transactions.export.composables.ExportBottomSheetModal
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AllTransactionsScreenNavArgs(
    val userId: Long
)

fun NavGraphBuilder.allTransactionsScreen(onNavigateBack: () -> Unit,
                                          onPromptForPin: () -> Unit,) {
    composable<AllTransactionsScreenNavArgs> { backStackEntry ->
        val args = backStackEntry.toRoute<AllTransactionsScreenNavArgs>()
        val viewModel = hiltViewModel<AllTransactionsScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart(args.userId)
            onStopOrDispose { }
        }

        AllTransactionsScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onNavigateBack = onNavigateBack,
            onTransactionClicked = viewModel::onTransactionClicked,
           onShowExportBottomSheet = viewModel::onShowExportBottomSheet,
            onDismissExportBottomSheet = viewModel::onHideExportBottomSheet,
            onPromptForPin = onPromptForPin,
            backStackEntry = backStackEntry,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    modifier: Modifier = Modifier,
    model: AllTransactionsScreenModel,
    onNavigateBack: () -> Unit,
    onTransactionClicked: (Long) -> Unit,
    onShowExportBottomSheet: () -> Unit,
    onDismissExportBottomSheet: () -> Unit,
    onPromptForPin: () -> Unit,
    backStackEntry: NavBackStackEntry?
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All Transactions",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if(model.transactions.isNotEmpty()){
                        IconButton(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    color = Color(0x1FFFFFFF),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            onClick = { onShowExportBottomSheet() }) {
                            Icon(
                                painter = painterResource(R.drawable.export),
                                contentDescription = "Export",
                                tint = LocalContentColor.current
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        when {
            model.isLoading -> {
                LoadingSpinner(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            model.isError -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            model.transactions.isEmpty() -> {
                EmptyTransactionsList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                val transactions = model.transactionGroups

                CompositionLocalProvider(
                    LocalTransactionFormatter provides TransactionFormatter(
                        useBracketsForExpense = model.userPreferences?.useBracketsForExpense ?: false,
                        currencySymbol = model.userPreferences?.currencySymbol ?: "$",
                        thousandsSeparator = model.userPreferences?.thousandsSeparator ?: ",",
                        decimalSeparator = model.userPreferences?.decimalSeparator ?: "."
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(transactions) { group ->
                            Spacer(modifier = Modifier.height(16.dp))

                            TransactionByDayItem(
                                formattedDate = group.dateLabel,
                                transactions = group.transactions,
                                selectedTransactionId = model.selectedTransactionId,
                                onTransactionClicked = onTransactionClicked
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (model.showExportBottomSheet) {
                        model.userId?.let { userId ->
                            ExportBottomSheetModal(
                                userId = userId,
                                onDismiss = { onDismissExportBottomSheet()},
                                onPromptForPin = onPromptForPin,
                                backStackEntry = backStackEntry,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTransactionsList(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💸",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No transactions to show",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllTransactionsScreenPreview() {
    val sampleTransactions = listOf(
        TransactionData(
            id = 1,
            userId = 1,
            amount = 158999,
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
            amount = 12550,
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
            amount = 500000,
            isExpense = false,
            name = "Salary Deposit",
            category = null,
            note = "Monthly salary payment",
            createdAt = LocalDateTime.now().minusDays(1),
            recurringFrequency = RecurringFrequency.MONTHLY
        )
    )

    val groups = listOf(
        TransactionGroup(
            dateLabel = "Today",
            transactions = sampleTransactions.take(2)
        ),
        TransactionGroup(
            dateLabel = "Yesterday",
            transactions = listOf(sampleTransactions[2])
        )
    )

    SpendLessTheme {
        AllTransactionsScreen(
            model = AllTransactionsScreenModel(
                userId = 1L,
                transactions = sampleTransactions,
                isLoading = false,
                isError = false,
                selectedTransactionId = 1L,
                transactionGroups = groups
            ),
            onNavigateBack = { },
            onTransactionClicked = { },
            onShowExportBottomSheet = { },
            onDismissExportBottomSheet = { },
            onPromptForPin = { },
            backStackEntry = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyTransactionsListPreview() {
    SpendLessTheme {
        EmptyTransactionsList(
            modifier = Modifier.fillMaxSize()
        )
    }
}