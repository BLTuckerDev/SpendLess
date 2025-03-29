package dev.bltucker.spendless.transactions.createtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.repositories.TransactionRepository
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.TransactionCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateTransactionScreenViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val mutableModel = MutableStateFlow(CreateTransactionScreenModel())
    val observableModel: StateFlow<CreateTransactionScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart(userId: Long) {
        if (hasStarted) {
            return
        }

        hasStarted = true
        mutableModel.update { it.copy(userId = userId, isLoading = true) }

        // Load user preferences
        viewModelScope.launch {
            try {
                val userPreferences = userRepository.getUserPreferences(userId)

                if (userPreferences != null) {
                    mutableModel.update { model ->
                        model.copy(
                            currencySymbol = userPreferences.currencySymbol,
                            decimalSeparator = userPreferences.decimalSeparator,
                            thousandsSeparator = userPreferences.thousandsSeparator,
                            useBracketsForExpense = userPreferences.useBracketsForExpense,
                            isLoading = false
                        )
                    }
                } else {
                    mutableModel.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to load user preferences"
                    )
                }
            }
        }
    }

    fun onTransactionTypeChanged(isExpense: Boolean) {
        mutableModel.update { it.copy(isExpense = isExpense) }
    }

    fun onReceiverChanged(receiver: String) {
        mutableModel.update { it.copy(receiver = receiver.trim()) }
    }

    fun onSenderChanged(sender: String) {
        mutableModel.update { it.copy(sender = sender.trim()) }
    }

    fun onAmountChanged(amount: String) {
        val currentModel = mutableModel.value
        val formattedAmount = currentModel.formatAmountForDisplay(amount)
        mutableModel.update { it.copy(amount = formattedAmount) }
    }

    fun onNoteChanged(note: String) {
        if (note.length <= 100) {
            mutableModel.update { it.copy(note = note) }
        }
    }

    fun onCategorySelected(category: TransactionCategory) {
        mutableModel.update {
            it.copy(
                selectedCategory = category,
                categoryMenuExpanded = false
            )
        }
    }

    fun onToggleCategoryMenu() {
        mutableModel.update { it.copy(categoryMenuExpanded = !it.categoryMenuExpanded) }
    }

    fun onRecurringFrequencySelected(frequency: RecurringFrequency) {
        mutableModel.update {
            it.copy(
                recurringFrequency = frequency,
                recurringFrequencyMenuExpanded = false
            )
        }
    }

    fun onToggleRecurringFrequencyMenu() {
        mutableModel.update { it.copy(recurringFrequencyMenuExpanded = !it.recurringFrequencyMenuExpanded) }
    }

    fun onCreateTransactionClick() {
        val currentModel = mutableModel.value

        if (!currentModel.canCreateTransaction()) {
            return
        }

        val userId = currentModel.userId ?: return

        viewModelScope.launch {

            val needsToReauth = userRepository.needsReauthentication()

            if(needsToReauth){
                mutableModel.update {
                    it.copy(shouldReauthenticate = true)
                }
                return@launch
            }

            mutableModel.update { it.copy(isLoading = true) }

            try {
                val transaction = TransactionData(
                    id = 0, // Will be set by Room
                    userId = userId,
                    amount = currentModel.getFormattedAmount(),
                    isExpense = currentModel.isExpense,
                    name = currentModel.getPersonName(),
                    category = if (currentModel.isExpense) currentModel.selectedCategory else null,
                    note = currentModel.note.takeIf { it.isNotBlank() },
                    createdAt = LocalDateTime.now(),
                    recurringFrequency = currentModel.recurringFrequency
                )

                transactionRepository.createTransaction(transaction)

                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        transactionCreated = true
                    )
                }
            } catch (e: Exception) {
                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Failed to create transaction: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetState() {
        mutableModel.update {
            CreateTransactionScreenModel(userId = it.userId)
        }
    }

    fun onTransactionCreatedHandled() {
        mutableModel.update { it.copy(transactionCreated = false) }
    }

    fun onClearShouldReauthenticate() {
        mutableModel.update { it.copy(shouldReauthenticate = false) }
    }
}