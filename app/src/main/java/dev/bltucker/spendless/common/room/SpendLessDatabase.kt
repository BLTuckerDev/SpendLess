package dev.bltucker.spendless.common.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SpendLessUser::class,
        UserPreferences::class,
        SecuritySettings::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SpendLessDatabase : RoomDatabase() {
    abstract fun spendLessUserDao(): SpendLessUserDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun securitySettingsDao(): SecuritySettingsDao

    companion object {
        const val DATABASE_NAME = "spendless.db"
    }
}