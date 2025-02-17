package dev.bltucker.spendless.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_preferences",
    foreignKeys = [
        ForeignKey(
            entity = SpendLessUser::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserPreferences(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "use_brackets_for_expense")
    val useBracketsForExpense: Boolean = true,

    @ColumnInfo(name = "currency_symbol")
    val currencySymbol: String = "$",

    @ColumnInfo(name = "decimal_separator")
    val decimalSeparator: String = ".",

    @ColumnInfo(name = "thousands_separator")
    val thousandsSeparator: String = ","
)