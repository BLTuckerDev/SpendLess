package dev.bltucker.spendless.transactions.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.TransactionRepository
import dev.bltucker.spendless.common.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExportScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val transactionExporter: TransactionExporter,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val mutableModel = MutableStateFlow(ExportScreenModel(isLoading = true))
    val observableModel: StateFlow<ExportScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart(userId: Long) {
        if (hasStarted) {
            return
        }

        hasStarted = true
        loadInitialData(userId)
    }

    private fun loadInitialData(userId: Long) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val availableMonths = generateAvailableMonths(now)

            mutableModel.update {
                it.copy(
                    userId = userId,
                    isLoading = false,
                    availableMonths = availableMonths
                )
            }
        }
    }

    private fun generateAvailableMonths(now: LocalDateTime): List<SpecificMonthOption> {
        val months = mutableListOf<SpecificMonthOption>()
        val currentMonth = now.month
        val currentYear = now.year

        for (monthValue in 1..currentMonth.value) {
            val month = java.time.Month.of(monthValue)
            val monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault())

            months.add(
                SpecificMonthOption(
                    month = monthName,
                    year = currentYear,
                    isCurrentMonth = month == currentMonth
                )
            )
        }

        return months.reversed()
    }

    fun onExportDateRangeDropdownToggle() {
        mutableModel.update {
            it.copy(isDateRangeDropdownExpanded = !it.isDateRangeDropdownExpanded)
        }
    }

    fun onExportDateRangeSelected(dateRange: ExportDateRange) {
        mutableModel.update {
            it.copy(
                exportDateRange = dateRange,
                isDateRangeDropdownExpanded = false,
                // If the selection is no longer "Specific Month", clear the specific month selection
                selectedSpecificMonth = if (dateRange != ExportDateRange.SPECIFIC_MONTH) null else it.selectedSpecificMonth
            )
        }
    }

    fun onFormatDropdownToggle() {
        mutableModel.update {
            it.copy(isFormatDropdownExpanded = !it.isFormatDropdownExpanded)
        }
    }

    fun onFormatSelected(format: ExportFormat) {
        mutableModel.update {
            it.copy(
                exportFormat = format,
                isFormatDropdownExpanded = false
            )
        }
    }

    fun onSpecificMonthDropdownToggle() {
        mutableModel.update {
            it.copy(isSpecificMonthDropdownExpanded = !it.isSpecificMonthDropdownExpanded)
        }
    }

    fun onSpecificMonthSelected(specificMonth: SpecificMonthOption) {
        mutableModel.update {
            it.copy(
                selectedSpecificMonth = specificMonth,
                isSpecificMonthDropdownExpanded = false,
                exportDateRange = ExportDateRange.SPECIFIC_MONTH
            )
        }
    }

    fun onExportClick() {
        viewModelScope.launch {

            val needsToReauth = userRepository.needsReauthentication()

            if(needsToReauth){
                mutableModel.update {
                    it.copy(shouldReauthenticate = true)
                }
                return@launch
            }

            val userId = mutableModel.value.userId ?: return@launch
            mutableModel.update {
                it.copy(isLoading = true)
            }

            try {

                transactionExporter.exportTransactions(
                    userId = userId,
                    dateRange = mutableModel.value.exportDateRange,
                    format = mutableModel.value.exportFormat,
                    specificMonth = mutableModel.value.selectedSpecificMonth
                )

                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        exportSuccessful = true
                    )
                }
            } catch (e: Exception) {
                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        exportError = "Failed to export: ${e.message}"
                    )
                }
            }
        }
    }

    fun onClearShouldReauthenticate(){
        mutableModel.update {
            it.copy(shouldReauthenticate = false)
        }
    }

    fun onExportComplete() {
        mutableModel.update {
            it.copy(exportSuccessful = false)
        }
    }

    fun onErrorDismissed() {
        mutableModel.update {
            it.copy(exportError = null)
        }
    }
}