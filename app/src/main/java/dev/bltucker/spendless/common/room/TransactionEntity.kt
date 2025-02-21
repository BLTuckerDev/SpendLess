package dev.bltucker.spendless.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = SpendLessUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "encrypted_amount")
    val encryptedAmount: String,

    @ColumnInfo(name = "is_expense")
    val isExpense: Boolean,

    @ColumnInfo(name = "encrypted_name")
    val encryptedName: String,

    @ColumnInfo(name = "encrypted_category")
    val encryptedCategory: String?,

    @ColumnInfo(name = "encrypted_note")
    val encryptedNote: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "recurring_frequency")
    val recurringFrequency: RecurringFrequency
)





enum class TransactionCategory {
    CLOTHING_AND_ACCESSORIES,
    EDUCATION,
    ENTERTAINMENT,
    FOOD_AND_GROCERIES,
    HEALTH_AND_WELLNESS,
    HOME,
    OTHER,
    PERSONAL_CARE,
    SAVINGS_AND_INVESTMENTS,
    TRANSPORTATION
}

enum class RecurringFrequency {
    DOES_NOT_REPEAT,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}