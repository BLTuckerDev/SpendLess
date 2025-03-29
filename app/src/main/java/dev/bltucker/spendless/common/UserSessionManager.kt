package dev.bltucker.spendless.common

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import dev.bltucker.spendless.common.repositories.UserRepository

@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: dagger.Lazy<UserRepository>,
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREF_NAME = "spendless_user_session"
        private const val KEY_LAST_LOGGED_IN_USER_ID = "last_logged_in_user_id"
        private const val KEY_SESSION_START_TIME = "session_start_time"
    }

    fun saveLastLoggedInUser(userId: Long) {
        prefs.edit() { putLong(KEY_LAST_LOGGED_IN_USER_ID, userId) }
        prefs.edit() { putLong(KEY_SESSION_START_TIME, System.currentTimeMillis()) }
    }

    fun getLastLoggedInUser(): Long? {
        val userId = prefs.getLong(KEY_LAST_LOGGED_IN_USER_ID, -1L)
        return if (userId == -1L) null else userId
    }

    fun getSessionStartTime(): Long? {
        val startTime = prefs.getLong(KEY_SESSION_START_TIME, -1L)
        return if (startTime == -1L) null else startTime
    }

    suspend fun needsReauthentication(): Boolean {
        val userId = getLastLoggedInUser() ?: return false
        val startTime = getSessionStartTime() ?: return true

        val securitySettings = userRepository.get().getUserSecuritySettings(userId) ?: return false

        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - startTime

        //TODO reset to real settings
        return sessionDuration >= 10_000 //securitySettings.sessionDurationMinutes * 60 * 1000

    }

    fun clearLastLoggedInUser() {
        prefs.edit() { remove(KEY_LAST_LOGGED_IN_USER_ID) }
        prefs.edit() { remove(KEY_SESSION_START_TIME) }
    }
}