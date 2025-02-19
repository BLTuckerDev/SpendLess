package dev.bltucker.spendless.common.repositories

import dev.bltucker.spendless.common.room.SecuritySettings
import dev.bltucker.spendless.common.room.SecuritySettingsDao
import dev.bltucker.spendless.common.room.SpendLessUser
import dev.bltucker.spendless.common.room.SpendLessUserDao
import dev.bltucker.spendless.common.room.UserPreferences
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

    suspend fun createUser(username: String, hashedPin: String, salt: String): Long {
        val user = SpendLessUser(username = username, pinHash = hashedPin, pinSalt = salt)
        val userId = userDao.insert(user)

        val defaultSettings = SecuritySettings(userId = userId)
        userSecurityDao.insert(defaultSettings)

        val defaultPreferences = UserPreferences(userId = userId)
        userPreferencesDao.insert(defaultPreferences)

        return userId
    }

    suspend fun getUserPreferences(userId: Long): UserPreferences? {
        return userPreferencesDao.getPreferencesForUser(userId)
    }

    suspend fun getUserSecuritySettings(userId: Long): SecuritySettings? {
        return userSecurityDao.getSettingsForUser(userId)
    }

    suspend fun updateUserPreferences(updatedPreferences: UserPreferences) {
        userPreferencesDao.update(updatedPreferences)
    }
}