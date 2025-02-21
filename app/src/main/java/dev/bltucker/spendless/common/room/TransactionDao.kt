package dev.bltucker.spendless.common.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDateTime

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query(value = "SELECT * FROM transactions WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getTransactionsForUser(userId: Long): List<Transaction>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    suspend fun getTransactionsForUserInDateRange(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Transaction>
}