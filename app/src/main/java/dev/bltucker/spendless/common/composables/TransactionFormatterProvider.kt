package dev.bltucker.spendless.common.composables

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

data class TransactionFormatter(
    val currencySymbol: String = "$",
    val thousandsSeparator: String = ",",
    val decimalSeparator: String = ".",
    val useBracketsForExpense: Boolean = false
) {
    fun formatAmount(amount: Long, isExpense: Boolean = false): String {
        val absoluteAmount = amount.toString()
            .padStart(3, '0')
            .let {
                val length = it.length

                "${it.substring(0, length - 2)}${decimalSeparator}${it.substring(length - 2)}"
            }
            .let { rawAmount ->
                rawAmount.split(decimalSeparator)[0]
                    .reversed()
                    .chunked(3)
                    .joinToString(thousandsSeparator)
                    .reversed() + decimalSeparator + rawAmount.split(decimalSeparator)[1]
            }

        return when {
            !isExpense -> "$currencySymbol$absoluteAmount"
            useBracketsForExpense -> "($currencySymbol$absoluteAmount)"
            else -> "-$currencySymbol$absoluteAmount"
        }
    }
}

val LocalTransactionFormatter = staticCompositionLocalOf {
    TransactionFormatter()
}