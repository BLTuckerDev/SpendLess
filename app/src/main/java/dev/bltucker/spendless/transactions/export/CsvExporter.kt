package dev.bltucker.spendless.transactions.export

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.bltucker.spendless.common.FileUtils
import dev.bltucker.spendless.common.repositories.TransactionData
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun exportTransactionsToCsv(
        transactions: List<TransactionData>,
        currencySymbol: String,
        useBracketsForExpense: Boolean
    ): File {
        val fileName = "spendless_export_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.csv"

        return FileUtils.saveFileToDownloads(
            context = context,
            fileName = fileName,
            mimeType = "text/csv"
        ) { outputStream ->
            outputStream.writer().use { writer ->
                writer.write("Date,Time,Name,Category,Amount,Note\n")

                transactions.forEach { transaction ->
                    val date = transaction.createdAt.format(dateFormatter)
                    val time = transaction.createdAt.format(timeFormatter)
                    val name = escapeCSV(transaction.name)
                    val category = escapeCSV(transaction.category?.displayName ?: "")

                    val amount = formatAmount(
                        amount = transaction.amount,
                        isExpense = transaction.isExpense,
                        currencySymbol = currencySymbol,
                        useBracketsForExpense = useBracketsForExpense
                    )

                    val note = escapeCSV(transaction.note ?: "")

                    writer.write("$date,$time,$name,$category,$amount,$note\n")
                }
            }
        }
    }

    /**
     * Escapes a string for CSV format by:
     * 1. Enclosing it in double-quotes
     * 2. Escaping any double-quotes inside by doubling them
     */
    private fun escapeCSV(text: String): String {
        val escaped = text.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    private fun formatAmount(
        amount: Long,
        isExpense: Boolean,
        currencySymbol: String,
        useBracketsForExpense: Boolean
    ): String {
        val formattedAmount = String.format(Locale.getDefault(), "%.2f", amount / 100.0)

        return if (isExpense) {
            if (useBracketsForExpense) {
                "\"($currencySymbol$formattedAmount)\""
            } else {
                "\"-$currencySymbol$formattedAmount\""
            }
        } else {
            "\"$currencySymbol$formattedAmount\""
        }
    }
}