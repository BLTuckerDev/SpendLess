package dev.bltucker.spendless.transactions.createtransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.common.theme.SpendLessTheme
import dev.bltucker.spendless.transactions.createtransaction.composables.AddNoteButton
import dev.bltucker.spendless.transactions.createtransaction.composables.CategoryDropdown
import dev.bltucker.spendless.transactions.createtransaction.composables.MoneyField
import dev.bltucker.spendless.transactions.createtransaction.composables.RecurringFrequencyDropdown
import dev.bltucker.spendless.transactions.createtransaction.composables.TransactionTextField
import dev.bltucker.spendless.transactions.createtransaction.composables.TransactionTypeSelector
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

const val CREATE_TRANSACTIONS_SCREEN_ROUTE = "createTransactions"

fun NavGraphBuilder.createTransactionsScreen(onNavigateBack: () -> Unit) {
    composable(
        route = "$CREATE_TRANSACTIONS_SCREEN_ROUTE/{userId}",
        arguments = listOf(
            navArgument("userId") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getLong("userId") ?: return@composable
        val viewModel = hiltViewModel<CreateTransactionScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart(userId)
            onStopOrDispose {}
        }

        LaunchedEffect(model.transactionCreated) {
            if (model.transactionCreated) {
                viewModel.onTransactionCreatedHandled()
                onNavigateBack()
            }
        }

        CreateTransactionContent(
            model = model,
            onTransactionTypeChanged = viewModel::onTransactionTypeChanged,
            onReceiverChanged = viewModel::onReceiverChanged,
            onSenderChanged = viewModel::onSenderChanged,
            onAmountChanged = viewModel::onAmountChanged,
            onNoteChanged = viewModel::onNoteChanged,
            onCategorySelected = viewModel::onCategorySelected,
            onToggleCategoryMenu = viewModel::onToggleCategoryMenu,
            onRecurringFrequencySelected = viewModel::onRecurringFrequencySelected,
            onToggleRecurringFrequencyMenu = viewModel::onToggleRecurringFrequencyMenu,
            onCreateClick = viewModel::onCreateTransactionClick,
            onDismiss = onNavigateBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionContent(
    modifier: Modifier = Modifier,
    model: CreateTransactionScreenModel,
    onTransactionTypeChanged: (Boolean) -> Unit,
    onReceiverChanged: (String) -> Unit,
    onSenderChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onNoteChanged: (String) -> Unit,
    onCategorySelected: (TransactionCategory) -> Unit,
    onToggleCategoryMenu: () -> Unit,
    onRecurringFrequencySelected: (RecurringFrequency) -> Unit,
    onToggleRecurringFrequencyMenu: () -> Unit,
    onCreateClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = {
            it != SheetValue.Hidden
        }
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!model.isLoading) onDismiss()
        },
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create Transaction",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TransactionTypeSelector(
                isExpense = model.isExpense,
                onTypeChanged = onTransactionTypeChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            TransactionTextField(
                value = if (model.isExpense) model.receiver else model.sender,
                onValueChange = if (model.isExpense) onReceiverChanged else onSenderChanged,
                placeholder = if (model.isExpense) "Receiver" else "Sender",
            )

            Spacer(modifier = Modifier.height(24.dp))

//            MoneyField(
//                amount = model.amount,
//                onAmountChange = onAmountChanged,
//                isExpense = model.isExpense,
//                currencySymbol = model.currencySymbol,
//                useBracketsForExpense = model.useBracketsForExpense,
//                decimalSeparator = model.decimalSeparator
//            )

            Spacer(modifier = Modifier.height(24.dp))

            AddNoteButton(
                note = model.note,
                onNoteChange = onNoteChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (model.isExpense) {
                CategoryDropdown(
                    selectedCategory = model.selectedCategory,
                    expanded = model.categoryMenuExpanded,
                    onToggleExpanded = onToggleCategoryMenu,
                    onCategorySelected = onCategorySelected
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            RecurringFrequencyDropdown(
                selectedFrequency = model.recurringFrequency,
                expanded = model.recurringFrequencyMenuExpanded,
                onToggleExpanded = onToggleRecurringFrequencyMenu,
                onFrequencySelected = onRecurringFrequencySelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = model.canCreateTransaction() && !model.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (model.isLoading) {
                LoadingSpinner(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTransactionContentPreview() {
    SpendLessTheme {
        val model = CreateTransactionScreenModel(
            userId = 1L,
            isExpense = true,
            receiver = "Netflix",
            amount = "19.99",
            selectedCategory = TransactionCategory.ENTERTAINMENT
        )

        Column {
            Text(
                text = "Preview: Cannot show full modal bottom sheet in preview",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Create Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                TransactionTypeSelector(
                    isExpense = model.isExpense,
                    onTypeChanged = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                TransactionTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = model.receiver,
                    onValueChange = {},
                    placeholder = "Receiver",
                )

                Spacer(modifier = Modifier.height(24.dp))

//                MoneyField(
//                    amount = model.amount,
//                    onAmountChange = {},
//                    isExpense = model.isExpense,
//                    currencySymbol = model.currencySymbol,
//                    useBracketsForExpense = model.useBracketsForExpense,
//                    decimalSeparator = model.decimalSeparator
//                )

                Spacer(modifier = Modifier.height(24.dp))

                AddNoteButton(
                    note = "",
                    onNoteChange = {}
                )

                Spacer(modifier = Modifier.height(24.dp))

                CategoryDropdown(
                    selectedCategory = model.selectedCategory,
                    expanded = false,
                    onToggleExpanded = {},
                    onCategorySelected = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                RecurringFrequencyDropdown(
                    selectedFrequency = RecurringFrequency.DOES_NOT_REPEAT,
                    expanded = false,
                    onToggleExpanded = {},
                    onFrequencySelected = {}
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        text = "Create",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}