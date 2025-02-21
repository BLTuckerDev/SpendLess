package dev.bltucker.spendless.common.hilt

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bltucker.spendless.common.room.SecuritySettingsDao
import dev.bltucker.spendless.common.room.SpendLessDatabase
import dev.bltucker.spendless.common.room.SpendLessUserDao
import dev.bltucker.spendless.common.room.TransactionDao
import dev.bltucker.spendless.common.room.UserPreferencesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideSpendLessDatabase(@ApplicationContext context: Context): SpendLessDatabase {
        return Room.databaseBuilder(
            context,
            SpendLessDatabase::class.java,
            SpendLessDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideSpendLessUserDao(database: SpendLessDatabase): SpendLessUserDao {
        return database.spendLessUserDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDao(database: SpendLessDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }

    @Provides
    @Singleton
    fun provideSecuritySettingsDao(database: SpendLessDatabase): SecuritySettingsDao {
        return database.securitySettingsDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: SpendLessDatabase): TransactionDao {
        return database.transactionDao()
    }
}