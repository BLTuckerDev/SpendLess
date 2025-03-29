package dev.bltucker.spendless.transactions.createtransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.bltucker.spendless.common.composables.LoadingSpinner
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.Primary
import dev.bltucker.spendless.transactions.createtransaction.composables.AddNoteButton
import dev.bltucker.spendless.transactions.createtransaction.composables.CategoryDropdown
import dev.bltucker.spendless.transactions.createtransaction.composables.RecurringFrequencyDropdown
import dev.bltucker.spendless.transactions.createtransaction.composables.TransactionTextField
import dev.bltucker.spendless.transactions.createtransaction.composables.TransactionTypeSelector

const val CREATE_TRANSACTIONS_SCREEN_ROUTE = "createTransactions"

data class CreateTransactionScreenActions(
    val onTransactionTypeChanged: (Boolean) -> Unit,
    val onReceiverChanged: (String) -> Unit,
    val onSenderChanged: (String) -> Unit,
    val onAmountChanged: (String) -> Unit,
    val onNoteChanged: (String) -> Unit,
    val onCategorySelected: (TransactionCategory) -> Unit,
    val onToggleCategoryMenu: () -> Unit,
    val onRecurringFrequencySelected: (RecurringFrequency) -> Unit,
    val onToggleRecurringFrequencyMenu: () -> Unit,
    val onCreateClick: () -> Unit,
    val onDismiss: () -> Unit,
)

fun createCreateTransactionRoute(userId: Long): String {
    return "$CREATE_TRANSACTIONS_SCREEN_ROUTE/$userId"
}

fun NavGraphBuilder.createTransactionsScreen(onNavigateBack: (Long) -> Unit,
                                             onPromptForPin: () -> Unit,) {
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
                onNavigateBack(userId)
            }
        }

        val actions = CreateTransactionScreenActions(
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
            onDismiss = {
                onNavigateBack(userId)
            }
        )

        CreateTransactionContent(
            model = model,
            actions = actions
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionContent(
    modifier: Modifier = Modifier,
    model: CreateTransactionScreenModel,
    actions: CreateTransactionScreenActions
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
        confirmValueChange = {
            it != SheetValue.Hidden
        }
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!model.isLoading) actions.onDismiss()
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
            Row(){
                Text(
                    text = "Create Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Spacer(modifier = Modifier.weight(1F))

                IconButton(onClick = { actions.onDismiss() }) {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Close",)
                }
            }

            TransactionTypeSelector(
                isExpense = model.isExpense,
                onTypeChanged = actions.onTransactionTypeChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            TransactionTextField(
                value = if (model.isExpense) model.receiver else model.sender,
                onValueChange = if (model.isExpense) actions.onReceiverChanged else actions.onSenderChanged,
                placeholder = if (model.isExpense) "Receiver" else "Sender",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TransactionTextField(
                value = model.amount,
                onValueChange = actions.onAmountChanged,
                placeholder = "Amount",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )


            Spacer(modifier = Modifier.height(24.dp))

            AddNoteButton(
                note = model.note,
                onNoteChange = actions.onNoteChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (model.isExpense) {
                CategoryDropdown(
                    selectedCategory = model.selectedCategory,
                    expanded = model.categoryMenuExpanded,
                    onToggleExpanded = actions.onToggleCategoryMenu,
                    onCategorySelected = actions.onCategorySelected
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            RecurringFrequencyDropdown(
                selectedFrequency = model.recurringFrequency,
                expanded = model.recurringFrequencyMenuExpanded,
                onToggleExpanded = actions.onToggleRecurringFrequencyMenu,
                onFrequencySelected = actions.onRecurringFrequencySelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = actions.onCreateClick,
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