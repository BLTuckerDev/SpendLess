package dev.bltucker.spendless.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.common.room.SecuritySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuritySettingsScreenViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val mutableModel = MutableStateFlow(
        SecuritySettingsModel(
            isLoading = true,
        )
    )

    val observableModel: StateFlow<SecuritySettingsModel> = mutableModel

    private var hasStarted = false

    fun onStart(userId: Long) {
        if (hasStarted) {
            return
        }

        hasStarted = true

        loadSecuritySettings(userId)
    }

    private fun loadSecuritySettings(userId: Long) {
        viewModelScope.launch {
            val settings = userRepository.getUserSecuritySettings(userId)

            if (settings == null) {
                updateModelWithError()
                return@launch
            }

            mutableModel.update {
                it.copy(
                    userId = userId,
                    isLoading = false,
                    isError = false,
                    sessionExpirationTimeMinutes = settings.sessionDurationMinutes,
                    lockedOutDurationSeconds = settings.lockoutDurationSeconds
                )
            }
        }
    }

    private fun updateModelWithError() {
        mutableModel.update {
            it.copy(isLoading = false, isError = true)
        }
    }

    fun onSessionDurationChange(minutes: Int) {
        mutableModel.update {
            it.copy(sessionExpirationTimeMinutes = minutes)
        }
    }

    fun onLockedOutDurationChange(seconds: Int) {
        mutableModel.update {
            it.copy(lockedOutDurationSeconds = seconds)
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val userId = mutableModel.value.userId ?: return@launch
            val sessionDuration = mutableModel.value.sessionExpirationTimeMinutes ?: return@launch
            val lockoutDuration = mutableModel.value.lockedOutDurationSeconds ?: return@launch

            val updatedSettings = SecuritySettings(
                userId = userId,
                sessionDurationMinutes = sessionDuration,
                lockoutDurationSeconds = lockoutDuration
            )

            userRepository.updateSecuritySettings(updatedSettings)
        }
    }
}