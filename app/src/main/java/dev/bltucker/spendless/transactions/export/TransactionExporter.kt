package dev.bltucker.spendless.transactions.export

import dev.bltucker.spendless.common.repositories.TransactionRepository
import dev.bltucker.spendless.common.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionExporter @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val pdfExporter: PdfExporter,
    private val csvExporter: CsvExporter
) {
    suspend fun exportTransactions(
        userId: Long,
        dateRange: ExportDateRange,
        format: ExportFormat,
        specificMonth: SpecificMonthOption? = null
    ): File = withContext(Dispatchers.IO) {
        // Get date range boundaries
        val (startDate, endDate) = getDateRange(dateRange, specificMonth)

        // Get transactions
        val transactions = transactionRepository.getTransactionsForDateRange(userId, startDate, endDate)

        // Get user preferences for formatting
        val userPreferences = userRepository.getUserPreferences(userId)
            ?: throw IllegalStateException("User preferences not found")

        // Export to file
        return@withContext when (format) {
            ExportFormat.CSV -> csvExporter.exportTransactionsToCsv(
                transactions,
                userPreferences.currencySymbol,
                userPreferences.useBracketsForExpense
            )
            ExportFormat.PDF -> pdfExporter.exportTransactionsToPdf(
                transactions,
                userPreferences.currencySymbol,
                userPreferences.useBracketsForExpense
            )
        }
    }

    private fun getDateRange(dateRange: ExportDateRange, specificMonth: SpecificMonthOption?): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()

        return when (dateRange) {
            ExportDateRange.ALL_DATA -> {
                // Set start date to distant past to get all data
                Pair(LocalDateTime.of(2000, 1, 1, 0, 0), now)
            }
            ExportDateRange.LAST_THREE_MONTHS -> {
                val startDate = now.minusMonths(3).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                Pair(startDate, now)
            }
            ExportDateRange.LAST_MONTH -> {
                val lastMonth = now.minusMonths(1)
                val startDate = lastMonth.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                val endDate = YearMonth.of(lastMonth.year, lastMonth.month).atEndOfMonth().atTime(23, 59, 59)
                Pair(startDate, endDate)
            }
            ExportDateRange.CURRENT_MONTH -> {
                val startDate = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                Pair(startDate, now)
            }
            ExportDateRange.SPECIFIC_MONTH -> {
                if (specificMonth == null) {
                    throw IllegalArgumentException("Specific month data is required when SPECIFIC_MONTH is selected")
                }

                // Convert month name to month number
                val monthNumber = java.time.Month.valueOf(specificMonth.month.uppercase()).value
                val startDate = LocalDateTime.of(specificMonth.year, monthNumber, 1, 0, 0)
                val endDate = YearMonth.of(specificMonth.year, monthNumber).atEndOfMonth().atTime(23, 59, 59)

                Pair(startDate, endDate)
            }
        }
    }
}