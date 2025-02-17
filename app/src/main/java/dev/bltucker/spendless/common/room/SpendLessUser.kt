package dev.bltucker.spendless.common.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spendless_users")
data class SpendLessUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "pin_hash")
    val pinHash: String,

    @ColumnInfo(name = "pin_salt")
    val pinSalt: String
)