package dev.bltucker.spendless.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bltucker.spendless.common.composables.LocalTransactionFormatter
import dev.bltucker.spendless.common.composables.TransactionFormatter
import dev.bltucker.spendless.common.composables.TransactionListItem
import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.theme.SpendLessTheme
import java.time.LocalDateTime

@Composable
fun TransactionByDayItem(modifier: Modifier = Modifier,
                         formattedDate: String,
                         transactions: List<TransactionData>,
                         selectedTransactionId: Long?,
                         onTransactionClicked: (Long) -> Unit) {
    val transactionFormatter = LocalTransactionFormatter.current
    Column(modifier = modifier.background(color = Color.Transparent)) {
        Text(text = formattedDate, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        transactions.forEach {
            TransactionListItem(
                modifier = Modifier.fillMaxWidth(),
                id = it.id,
                name = it.name,
                category = it.category ?: TransactionCategory.OTHER,
                amount = transactionFormatter.formatAmount(it.amount, it.isExpense),
                isExpense = it.isExpense,
                note = it.note,
                isSelected = selectedTransactionId == it.id,
                onItemClick = onTransactionClicked
            )
        }

    }
}



@Preview(showBackground = true)
@Composable
private fun TransactionByDayItemPreview() {

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


        CompositionLocalProvider(
            LocalTransactionFormatter provides transactionFormatter
        ) {
            Surface(
                modifier = Modifier.padding(16.dp),
                color = Color(0xFFFEF7FF)
            ) {
                TransactionByDayItem(
                    modifier = Modifier.fillMaxWidth(),
                    formattedDate = "Today",
                    transactions = sampleTransactions,
                    onTransactionClicked = {},
                    selectedTransactionId = 1,
                )
            }
        }
    }
}