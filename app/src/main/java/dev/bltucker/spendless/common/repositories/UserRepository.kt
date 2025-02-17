package dev.bltucker.spendless.common.repositories

import dev.bltucker.spendless.common.room.SecuritySettingsDao
import dev.bltucker.spendless.common.room.SpendLessUserDao
import dev.bltucker.spendless.common.room.UserPreferencesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: SpendLessUserDao,
    private val userSecurityDao: SecuritySettingsDao,
    private val userPreferencesDao: UserPreferencesDao,
    ) {

    suspend fun getUser(username: String) = userDao.getUserByUsername(username)

}