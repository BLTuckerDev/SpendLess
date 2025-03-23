package dev.bltucker.spendless.transactions.createtransaction

import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory

data class CreateTransactionScreenModel(
    val userId: Long? = null,
    val isExpense: Boolean = true,
    val receiver: String = "",
    val sender: String = "",
    val amount: String = "",
    val note: String = "",
    val selectedCategory: TransactionCategory = TransactionCategory.OTHER,
    val recurringFrequency: RecurringFrequency = RecurringFrequency.DOES_NOT_REPEAT,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val transactionCreated: Boolean = false,
    val categoryMenuExpanded: Boolean = false,
    val recurringFrequencyMenuExpanded: Boolean = false,
    val currencySymbol: String = "$",
    val decimalSeparator: String = ".",
    val thousandsSeparator: String = ",",
    val useBracketsForExpense: Boolean = false
) {
    fun isValidName(): Boolean {
        val name = if (isExpense) receiver else sender
        return name.length in 3..14 && name.all { it.isLetterOrDigit() || it.isWhitespace() }
    }

    fun isValidAmount(): Boolean {
        return amount.isNotEmpty() && amount != "00.00" && amount != "0.00"
    }

    fun canCreateTransaction(): Boolean {
        return isValidName() && isValidAmount()
    }

    fun getFormattedAmount(): Long {
        if (amount.isEmpty() || amount == "00${decimalSeparator}00" || amount == "0${decimalSeparator}00") {
            return 0L
        }

        return try {
            val cleanedAmount = amount.replace(thousandsSeparator, "")
            val amountString = cleanedAmount.replace(decimalSeparator, ".")
            val dotIndex = amountString.indexOf('.')

            if (dotIndex == -1) {
                (amountString.toLong()) * 100
            } else {
                val dollars = amountString.substring(0, dotIndex).toLongOrNull() ?: 0L
                val cents = amountString.substring(dotIndex + 1).padEnd(2, '0').take(2)
                (dollars * 100) + cents.toLong()
            }
        } catch (e: NumberFormatException) {
            0L
        }
    }

    fun formatAmountForDisplay(rawAmount: String): String {
        if (rawAmount.isEmpty()) return ""

        val cleanInput = rawAmount.fold("") { acc, char ->
            when {
                char.isDigit() -> acc + char
                char.toString() == decimalSeparator && !acc.contains(decimalSeparator) -> acc + char
                else -> acc
            }
        }

        val parts = cleanInput.split(decimalSeparator)
        val wholePart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""

        val formattedWholePart = if (wholePart.isNotEmpty()) {
            wholePart.reversed()
                .chunked(3)
                .joinToString(thousandsSeparator)
                .reversed()
        } else {
            "0"
        }

        return if (decimalPart.isNotEmpty()) {
            "$formattedWholePart$decimalSeparator$decimalPart"
        } else if (cleanInput.endsWith(decimalSeparator)) {
            "$formattedWholePart$decimalSeparator"
        } else {
            formattedWholePart
        }
    }

    fun getPersonName(): String {
        return if (isExpense) receiver else sender
    }
}