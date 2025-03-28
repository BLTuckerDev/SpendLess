package dev.bltucker.spendless.common.repositories

import dev.bltucker.spendless.common.UserSessionManager
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
    private val userSessionManager: UserSessionManager,
    ) {

    fun saveLastLoggedInUser(userId: Long) {
        userSessionManager.saveLastLoggedInUser(userId)
    }

    fun getLastLoggedInUser(): Long? {
        return userSessionManager.getLastLoggedInUser()
    }

    fun clearLastLoggedInUser() {
        userSessionManager.clearLastLoggedInUser()
    }

    suspend fun needsReauthentication(): Boolean {
        return userSessionManager.needsReauthentication()
    }

    suspend fun getUser(username: String) = userDao.getUserByUsername(username)

    suspend fun getUserById(userId: Long) = userDao.getUserById(userId)

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

    suspend fun updateSecuritySettings(updatedSettings: SecuritySettings) {
        userSecurityDao.update(updatedSettings)
    }
}