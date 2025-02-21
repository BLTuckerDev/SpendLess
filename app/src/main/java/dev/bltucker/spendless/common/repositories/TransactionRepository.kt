package dev.bltucker.spendless.common.repositories

import dev.bltucker.spendless.common.room.RecurringFrequency
import dev.bltucker.spendless.common.room.Transaction
import dev.bltucker.spendless.common.room.TransactionCategory
import dev.bltucker.spendless.common.room.TransactionDao
import dev.bltucker.spendless.common.TransactionEncryptor
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

data class TransactionData(
    val id: Long,
    val userId: Long,
    val amount: Long,
    val isExpense: Boolean,
    val name: String,
    val category: TransactionCategory?,
    val note: String?,
    val createdAt: LocalDateTime,
    val recurringFrequency: RecurringFrequency
)

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val encryption: TransactionEncryptor
) {
    suspend fun createTransaction(data: TransactionData): Long {
        val transaction = Transaction(
            userId = data.userId,
            encryptedAmount = encryption.encrypt(data.amount.toString()),
            isExpense = data.isExpense,
            encryptedName = encryption.encrypt(data.name),
            encryptedCategory = data.category?.let { encryption.encrypt(it.name) },
            encryptedNote = data.note?.let { encryption.encrypt(it) },
            createdAt = data.createdAt,
            recurringFrequency = data.recurringFrequency
        )

        return transactionDao.insert(transaction)
    }

    suspend fun getTransaction(id: Long): TransactionData? {
        val transaction = transactionDao.getTransactionById(id) ?: return null

        return TransactionData(
            id = transaction.id,
            userId = transaction.userId,
            amount = encryption.decrypt(transaction.encryptedAmount).toLong(),
            isExpense = transaction.isExpense,
            name = encryption.decrypt(transaction.encryptedName),
            category = transaction.encryptedCategory?.let {
                TransactionCategory.valueOf(encryption.decrypt(it))
            },
            note = transaction.encryptedNote?.let { encryption.decrypt(it) },
            createdAt = transaction.createdAt,
            recurringFrequency = transaction.recurringFrequency
        )
    }

    suspend fun getTransactionsForUser(userId: Long): List<TransactionData> {
        return transactionDao.getTransactionsForUser(userId).map { transaction ->
            TransactionData(
                id = transaction.id,
                userId = transaction.userId,
                amount = encryption.decrypt(transaction.encryptedAmount).toLong(),
                isExpense = transaction.isExpense,
                name = encryption.decrypt(transaction.encryptedName),
                category = transaction.encryptedCategory?.let {
                    TransactionCategory.valueOf(encryption.decrypt(it))
                },
                note = transaction.encryptedNote?.let { encryption.decrypt(it) },
                createdAt = transaction.createdAt,
                recurringFrequency = transaction.recurringFrequency
            )
        }
    }

    suspend fun getPreviousWeekTransactions(userId: Long): List<TransactionData> {
        val now = LocalDateTime.now()
        val sunday = now.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val saturday = sunday.plusDays(6)

        return getTransactionsForDateRange(userId, sunday, saturday)
    }

    suspend fun getTransactionsForDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<TransactionData> {
        return transactionDao.getTransactionsForUserInDateRange(userId, startDate, endDate)
            .map { transaction ->
                TransactionData(
                    id = transaction.id,
                    userId = transaction.userId,
                    amount = encryption.decrypt(transaction.encryptedAmount).toLong(),
                    isExpense = transaction.isExpense,
                    name = encryption.decrypt(transaction.encryptedName),
                    category = transaction.encryptedCategory?.let {
                        TransactionCategory.valueOf(encryption.decrypt(it))
                    },
                    note = transaction.encryptedNote?.let { encryption.decrypt(it) },
                    createdAt = transaction.createdAt,
                    recurringFrequency = transaction.recurringFrequency
                )
            }
    }
}