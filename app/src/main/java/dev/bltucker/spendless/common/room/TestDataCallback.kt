package dev.bltucker.spendless.common.room

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.bltucker.spendless.common.TransactionEncryptor
import dev.bltucker.spendless.login.PinConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class TestDataCallback @Inject constructor(
    private val pinConverter: PinConverter,
    private val transactionEncryptor: TransactionEncryptor,
    private val spendLessUserDao: dagger.Lazy<SpendLessUserDao>,
    private val userPreferencesDao: dagger.Lazy<UserPreferencesDao>,
    private val securitySettingsDao: dagger.Lazy<SecuritySettingsDao>,
    private val transactionDao: dagger.Lazy<TransactionDao>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("TestDataCallback", "Creating test data...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                createTestUser()
                Log.d("TestDataCallback", "Test data creation completed successfully")
            } catch (e: Exception) {
                Log.e("TestDataCallback", "Error creating test data", e)
            }
        }
    }

    private suspend fun createTestUser() {
        // Create test user with PIN 12345
        val hashResult = pinConverter.hashPin("12345")
            ?: throw IllegalStateException("Failed to hash PIN")

        val userId = spendLessUserDao.get().insert(
            SpendLessUser(
                username = "testUser",
                pinHash = hashResult.hash,
                pinSalt = hashResult.salt
            )
        )

        createUserPreferences(userId)
        createSecuritySettings(userId)
        createTransactionHistory(userId)
    }

    private suspend fun createUserPreferences(userId: Long) {
        userPreferencesDao.get().insert(
            UserPreferences(
                userId = userId,
                useBracketsForExpense = true,
                currencySymbol = "$",
                decimalSeparator = ".",
                thousandsSeparator = ","
            )
        )
    }

    private suspend fun createSecuritySettings(userId: Long) {
        securitySettingsDao.get().insert(
            SecuritySettings(
                userId = userId,
                sessionDurationMinutes = 5,
                lockoutDurationSeconds = 30,
                biometricsEnabled = false
            )
        )
    }

    private suspend fun createTransactionHistory(userId: Long) {
        val today = LocalDateTime.now()

        // Create bi-monthly salary deposits
        createSalaryDeposits(userId, today)

        // Create monthly bills
        createMonthlyBills(userId, today)

        // Create daily transactions for the past 30 days
        createDailyTransactions(userId, today)
    }

    private suspend fun createSalaryDeposits(userId: Long, baseDate: LocalDateTime) {
        val firstPaycheck = createTransaction(
            userId = userId,
            amount = 500_000,
            isExpense = false,
            name = "Salary Deposit",
            category = null,
            note = "Bi-monthly salary payment",
            date = baseDate.withDayOfMonth(1),
            recurringFrequency = RecurringFrequency.MONTHLY
        )

        val secondPaycheck = createTransaction(
            userId = userId,
            amount = 500_000,
            isExpense = false,
            name = "Salary Deposit",
            category = null,
            note = "Bi-monthly salary payment",
            date = baseDate.withDayOfMonth(15),
            recurringFrequency = RecurringFrequency.MONTHLY
        )

        transactionDao.get().insert(firstPaycheck)
        transactionDao.get().insert(secondPaycheck)
    }

    private suspend fun createMonthlyBills(userId: Long, baseDate: LocalDateTime) {
        val monthlyBills = listOf(
            Triple("Rent Payment", 200_000L, TransactionCategory.HOME),
            Triple("Utilities", 15_000L, TransactionCategory.HOME),
            Triple("Internet Bill", 8_000L, TransactionCategory.HOME)
        )

        monthlyBills.forEachIndexed { index, (name, amount, category) ->
            val transaction = createTransaction(
                userId = userId,
                amount = amount,
                isExpense = true,
                name = name,
                category = category,
                note = "Monthly $name",
                date = baseDate.withDayOfMonth(index + 1),
                recurringFrequency = RecurringFrequency.MONTHLY
            )
            transactionDao.get().insert(transaction)
        }
    }

    private suspend fun createDailyTransactions(userId: Long, baseDate: LocalDateTime) {
        val dailyTransactions = listOf(
            Triple("Groceries", 4_500L, TransactionCategory.FOOD_AND_GROCERIES),
            Triple("Coffee Shop", 2_500L, TransactionCategory.FOOD_AND_GROCERIES),
            Triple("Restaurant", 3_500L, TransactionCategory.FOOD_AND_GROCERIES),
            Triple("Transportation", 3_000L, TransactionCategory.TRANSPORTATION)
        )

        // Generate 2-3 transactions per day for the last 30 days
        for (daysAgo in 30 downTo 0) {
            val transactionsForDay = (2..3).random()

            repeat(transactionsForDay) {
                val (name, amount, category) = dailyTransactions.random()
                val randomizedAmount = (amount * (0.8 + Math.random() * 0.4)).toLong() // +/- 20%

                val transaction = createTransaction(
                    userId = userId,
                    amount = randomizedAmount,
                    isExpense = true,
                    name = name,
                    category = category,
                    note = "Daily expense",
                    date = baseDate.minusDays(daysAgo.toLong())
                        .withHour((9 + it * 4).coerceAtMost(20))
                        .withMinute((0..59).random()),
                    recurringFrequency = RecurringFrequency.DOES_NOT_REPEAT
                )

                transactionDao.get().insert(transaction)
            }
        }
    }

    private fun createTransaction(
        userId: Long,
        amount: Long,
        isExpense: Boolean,
        name: String,
        category: TransactionCategory?,
        note: String,
        date: LocalDateTime,
        recurringFrequency: RecurringFrequency = RecurringFrequency.DOES_NOT_REPEAT
    ): Transaction {
        return Transaction(
            userId = userId,
            encryptedAmount = transactionEncryptor.encrypt(amount.toString()),
            isExpense = isExpense,
            encryptedName = transactionEncryptor.encrypt(name),
            encryptedCategory = category?.let { transactionEncryptor.encrypt(it.name) },
            encryptedNote = transactionEncryptor.encrypt(note),
            createdAt = date,
            recurringFrequency = recurringFrequency
        )
    }
}