package dev.bltucker.spendless.transactions.export

import android.content.Context
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import dev.bltucker.spendless.common.FileUtils
import dev.bltucker.spendless.common.repositories.TransactionData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")

    private val primaryColor = DeviceRgb(106, 0, 206) // #6A00CE
    private val headerBgColor = DeviceRgb(234, 221, 255) // #EADDFF

    fun exportTransactionsToPdf(
        transactions: List<TransactionData>,
        currencySymbol: String,
        useBracketsForExpense: Boolean
    ): File {
        val fileName = "spendless_export_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.pdf"

        return FileUtils.saveFileToDownloads(
            context = context,
            fileName = fileName,
            mimeType = "application/pdf"
        ) { outputStream ->
            val pdfWriter = PdfWriter(outputStream)
            val pdf = PdfDocument(pdfWriter)
            val document = Document(pdf, PageSize.A4)
            document.setMargins(36f, 36f, 36f, 36f)

            addHeader(document)

            // Group transactions by date
            val groupedTransactions = transactions
                .groupBy { LocalDate.from(it.createdAt) }
                .toSortedMap(compareByDescending { it })

            // Process each group
            groupedTransactions.forEach { (date, transactionsForDate) ->
                addDateHeader(document, date)
                addTransactionsTable(document, transactionsForDate, currencySymbol, useBracketsForExpense)
            }

            addSummary(document, transactions, currencySymbol, useBracketsForExpense)

            document.close()
        }
    }

    private fun addHeader(document: Document) {
        val title = Paragraph("SpendLess Transactions")
            .setFontSize(18f)
            .setFontColor(primaryColor)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)

        val subtitle = Paragraph("Generated on ${LocalDateTime.now().format(dateTimeFormatter)}")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)

        document.add(title)
        document.add(subtitle)
    }

    private fun addDateHeader(document: Document, date: LocalDate) {
        val dateHeader = Paragraph(date.format(dateFormatter))
            .setFontSize(14f)
            .setBold()
            .setFontColor(primaryColor)
            .setMarginTop(15f)
            .setMarginBottom(5f)

        document.add(dateHeader)
    }

    private fun addTransactionsTable(
        document: Document,
        transactions: List<TransactionData>,
        currencySymbol: String,
        useBracketsForExpense: Boolean
    ) {
        // Create table with 4 columns (Time, Name, Category, Amount)
        val table = Table(UnitValue.createPercentArray(floatArrayOf(15f, 35f, 25f, 25f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setBorder(Border.NO_BORDER)

        // Add header row
        table.addHeaderCell(createHeaderCell("Time"))
        table.addHeaderCell(createHeaderCell("Name"))
        table.addHeaderCell(createHeaderCell("Category"))
        table.addHeaderCell(createHeaderCell("Amount").setTextAlignment(TextAlignment.RIGHT))

        transactions.forEach { transaction ->
            val timeCell = createCell(transaction.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")))

            val nameCell = createCell(transaction.name)

            val categoryCell = createCell(transaction.category?.displayName ?: "")

            val amount = formatAmount(
                amount = transaction.amount,
                isExpense = transaction.isExpense,
                currencySymbol = currencySymbol,
                useBracketsForExpense = useBracketsForExpense
            )

            val amountCell = createCell(amount)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(if (transaction.isExpense) ColorConstants.RED else ColorConstants.DARK_GRAY)

            table.addCell(timeCell)
            table.addCell(nameCell)
            table.addCell(categoryCell)
            table.addCell(amountCell)

            // Add note if present (spanning all columns)
            if (!transaction.note.isNullOrBlank()) {
                val noteCell = Cell(1, 4) // 1 row, 4 columns
                    .add(Paragraph("Note: ${transaction.note}")
                        .setFontSize(9f)
                        .setFontColor(ColorConstants.GRAY))
                    .setBorder(Border.NO_BORDER)
                    .setBorderTop(Border.NO_BORDER)

                table.addCell(noteCell)
            }
        }

        document.add(table)
    }

    private fun addSummary(
        document: Document,
        transactions: List<TransactionData>,
        currencySymbol: String,
        useBracketsForExpense: Boolean
    ) {
        val income = transactions.filter { !it.isExpense }.sumOf { it.amount }
        val expenses = transactions.filter { it.isExpense }.sumOf { it.amount }
        val balance = income - expenses

        document.add(Paragraph("Summary").setBold().setFontSize(14f).setMarginTop(20f))

        val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(70f, 30f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setBorder(Border.NO_BORDER)

        summaryTable.addCell(createSummaryLabelCell("Total Income:"))
        summaryTable.addCell(createSummaryValueCell(formatAmount(income, false, currencySymbol, false))
            .setFontColor(ColorConstants.DARK_GRAY))

        summaryTable.addCell(createSummaryLabelCell("Total Expenses:"))
        summaryTable.addCell(createSummaryValueCell(formatAmount(expenses, true, currencySymbol, useBracketsForExpense))
            .setFontColor(ColorConstants.RED))

        summaryTable.addCell(createSummaryLabelCell("Balance:").setBold())
        summaryTable.addCell(createSummaryValueCell(formatAmount(balance, balance < 0, currencySymbol, useBracketsForExpense))
            .setBold()
            .setFontColor(if (balance < 0) ColorConstants.RED else ColorConstants.DARK_GRAY))

        document.add(summaryTable)
    }

    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBackgroundColor(headerBgColor)
            .setBold()
            .setPadding(5f)
            .setTextAlignment(TextAlignment.LEFT)
            .setVerticalAlignment(VerticalAlignment.MIDDLE)
    }

    private fun createCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBorder(Border.NO_BORDER)
            .setBorderBottom(SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
            .setPadding(5f)
    }

    private fun createSummaryLabelCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBorder(Border.NO_BORDER)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.RIGHT)
    }

    private fun createSummaryValueCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text))
            .setBorder(Border.NO_BORDER)
            .setPadding(5f)
            .setTextAlignment(TextAlignment.RIGHT)
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
                "($currencySymbol$formattedAmount)"
            } else {
                "-$currencySymbol$formattedAmount"
            }
        } else {
            "$currencySymbol$formattedAmount"
        }
    }
}