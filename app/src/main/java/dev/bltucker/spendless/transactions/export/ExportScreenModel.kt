package dev.bltucker.spendless.transactions.export

enum class ExportDateRange(val displayName: String) {
    ALL_DATA("All data"),
    LAST_THREE_MONTHS("Last three months"),
    LAST_MONTH("Last month"),
    CURRENT_MONTH("Current month"),
    SPECIFIC_MONTH("Specific Month")
}

enum class ExportFormat(val displayName: String) {
    CSV("CSV"),
    PDF("PDF")
}

data class SpecificMonthOption(
    val month: String,
    val year: Int,
    val isCurrentMonth: Boolean = false
)

data class ExportScreenModel(
    val userId: Long? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val exportDateRange: ExportDateRange = ExportDateRange.LAST_THREE_MONTHS,
    val exportFormat: ExportFormat = ExportFormat.CSV,
    val isDateRangeDropdownExpanded: Boolean = false,
    val isFormatDropdownExpanded: Boolean = false,
    val isSpecificMonthDropdownExpanded: Boolean = false,
    val availableMonths: List<SpecificMonthOption> = emptyList(),
    val selectedSpecificMonth: SpecificMonthOption? = null,
    val exportSuccessful: Boolean = false,
    val exportError: String? = null
)