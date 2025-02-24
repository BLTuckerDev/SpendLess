package dev.bltucker.spendless.dashboard

import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.SpendLessUser
import dev.bltucker.spendless.common.room.UserPreferences
import kotlin.math.absoluteValue

data class DashboardScreenModel(
    val user: SpendLessUser? = null,
    val userPreferences: UserPreferences? = null,
    val transactions: List<TransactionData> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {

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


