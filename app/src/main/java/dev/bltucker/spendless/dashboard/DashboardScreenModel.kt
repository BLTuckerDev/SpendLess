package dev.bltucker.spendless.dashboard

import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.SpendLessUser
import dev.bltucker.spendless.common.room.UserPreferences
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")


data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<TransactionData>
)

enum class ReAuthAction{
    SHOW_ALL,
    FAB,
    SETTINGS,
}

data class DashboardScreenModel(
    val user: SpendLessUser? = null,
    val userPreferences: UserPreferences? = null,
    val transactions: List<TransactionData> = emptyList(),
    val clickedTransactionId: Long? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val showExportBottomSheet: Boolean = false,
    val shouldReauthenticate: Boolean = false,
    val reAuthAction: ReAuthAction? = null,
) {

    fun formatTransactionDate(date: LocalDateTime): String {
        val today = LocalDate.now()
        val transactionDate = date.toLocalDate()

        return when(transactionDate) {
            today -> "TODAY"
            today.minusDays(1) -> "YESTERDAY"
            else -> date.format(dateFormatter)
        }
    }

    val transactionsGroupedByDate: List<TransactionGroup> by lazy {
        transactions
            .groupBy { it.createdAt.toLocalDate() }
            .map { (date, transactionsForDate) ->
                val transactionGroup = TransactionGroup(
                    dateLabel = formatTransactionDate(date.atStartOfDay()),
                    transactions = transactionsForDate.sortedByDescending { it.createdAt }
                )
                transactionGroup
            }
            .sortedByDescending { it.transactions.first().createdAt }
    }


    val accountBalance: Long by lazy {
        transactions.fold(0L) { balance, transaction ->
            if (transaction.isExpense) {
                balance - transaction.amount
            } else {
                balance + transaction.amount
            }
        }
    }


    fun formattedAccountBalance(): String {
        if (userPreferences == null) {
            return ""
        }

        val currencySymbol = userPreferences.currencySymbol
        val thousandsSeparator = userPreferences.thousandsSeparator
        val decimalSeparator = userPreferences.decimalSeparator
        val useBrackets = userPreferences.useBracketsForExpense

        val dollars = accountBalance.absoluteValue / 100
        val cents = accountBalance.absoluteValue % 100

        val formattedDollars = dollars.toString()
            .reversed()
            .chunked(3)
            .joinToString(thousandsSeparator)
            .reversed()

        val formattedCents = cents.toString().padStart(2, '0')

        val formattedAmount = "$formattedDollars$decimalSeparator$formattedCents"

        return when {
            accountBalance < 0 && useBrackets -> "$currencySymbol($formattedAmount)"
            accountBalance < 0 -> "-$currencySymbol$formattedAmount"
            else -> "$currencySymbol$formattedAmount"
        }
    }

}


