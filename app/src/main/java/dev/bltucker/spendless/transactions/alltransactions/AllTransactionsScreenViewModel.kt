package dev.bltucker.spendless.transactions.alltransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.repositories.TransactionRepository
import dev.bltucker.spendless.common.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AllTransactionsScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val mutableModel = MutableStateFlow(AllTransactionsScreenModel())
    val observableModel: StateFlow<AllTransactionsScreenModel> = mutableModel

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

    private var hasStarted = false

    fun onStart(userId: Long) {
        if (hasStarted) {
            return
        }

        hasStarted = true
        loadUserPreferences(userId)
        loadTransactions(userId)
    }

    private fun loadUserPreferences(userId: Long){
        viewModelScope.launch {
            val userPreferences = userRepository.getUserPreferences(userId)
            mutableModel.update { it.copy(userPreferences = userPreferences) }
        }
    }

    private fun loadTransactions(userId: Long) {
        viewModelScope.launch {
            mutableModel.update { it.copy(isLoading = true, userId = userId) }

            try {
                transactionRepository.getTransactionsForUser(userId).collect { transactions ->
                    val groups = groupTransactionsByDate(transactions)
                    mutableModel.update {
                        it.copy(
                            isLoading = false,
                            transactions = transactions,
                            transactionGroups = groups
                        )
                    }
                }
            } catch (e: Exception) {
                mutableModel.update { it.copy(isLoading = false, isError = true) }
            }
        }
    }

    private fun groupTransactionsByDate(transactions: List<TransactionData>): List<TransactionGroup> {
        return transactions
            .groupBy { it.createdAt.toLocalDate() }
            .map { (date, transactionsForDate) ->
                TransactionGroup(
                    dateLabel = formatTransactionDate(date),
                    transactions = transactionsForDate.sortedByDescending { it.createdAt }
                )
            }
            .sortedByDescending { getDateFromLabel(it.dateLabel) }
    }

    private fun getDateFromLabel(dateLabel: String): LocalDate {
        return when (dateLabel) {
            "Today" -> LocalDate.now()
            "Yesterday" -> LocalDate.now().minusDays(1)
            else -> LocalDate.parse(dateLabel, dateFormatter)
        }
    }

    private fun formatTransactionDate(date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> date.format(dateFormatter)
        }
    }

    fun onTransactionClicked(transactionId: Long) {
        mutableModel.update { currentModel ->
            if (currentModel.selectedTransactionId == transactionId) {
                currentModel.copy(selectedTransactionId = null)
            } else {
                currentModel.copy(selectedTransactionId = transactionId)
            }
        }
    }
}