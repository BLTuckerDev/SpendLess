package dev.bltucker.spendless.common.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        SpendLessUser::class,
        UserPreferences::class,
        SecuritySettings::class,
        Transaction::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class SpendLessDatabase : RoomDatabase() {
    abstract fun spendLessUserDao(): SpendLessUserDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun securitySettingsDao(): SecuritySettingsDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "spendless.db"
    }
}