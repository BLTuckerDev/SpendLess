package dev.bltucker.spendless.transactions.alltransactions

import dev.bltucker.spendless.common.repositories.TransactionData
import dev.bltucker.spendless.common.room.UserPreferences

data class AllTransactionsScreenModel(
    val userId: Long? = null,
    val userPreferences: UserPreferences? = null,
    val transactions: List<TransactionData> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val selectedTransactionId: Long? = null,
    val transactionGroups: List<TransactionGroup> = emptyList(),
    val showExportBottomSheet: Boolean = false,
)

data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<TransactionData>
)