package dev.bltucker.spendless.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.UserSessionManager
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.login.PinConverter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val pinConverter: PinConverter,
) : ViewModel() {

    private val mutableModel = MutableStateFlow(AuthenticationScreenModel())
    val observableModel: StateFlow<AuthenticationScreenModel> = mutableModel

    private var hasStarted = false
    private var bannerDismissJob: Job? = null

    fun onStart() {
        if (hasStarted) {
            return
        }

        hasStarted = true

        val userId = userRepository.getLastLoggedInUser()

        if (userId == null) {
            mutableModel.update {
                it.copy(isError = true, errorMessage = "User ID is required")
            }
            return
        }

        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                if (user == null) {
                    mutableModel.update {
                        it.copy(isError = true, errorMessage = "User not found")
                    }
                    return@launch
                }

                val securitySettings = userRepository.getUserSecuritySettings(userId)
                if (securitySettings == null) {
                    mutableModel.update {
                        it.copy(isError = true, errorMessage = "Security settings not found")
                    }
                    return@launch
                }

                mutableModel.update {
                    it.copy(
                        userId = userId,
                        username = user.username,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                mutableModel.update {
                    it.copy(isLoading = false, isError = true, errorMessage = "Error loading user data")
                }
            }
        }
    }

    fun onPinDigitEntered(digit: String) {
        val currentModel = mutableModel.value

        if (currentModel.isLocked) {
            return
        }

        if (currentModel.pin.length < 5) {
            mutableModel.update {
                it.copy(pin = it.pin + digit, isError = false, errorMessage = null)
            }

            if (mutableModel.value.pin.length == 5) {
                verifyPin()
            }
        }
    }

    fun onDeletePinDigit() {
        if (mutableModel.value.pin.isNotEmpty()) {
            mutableModel.update {
                it.copy(pin = it.pin.dropLast(1), isError = false, errorMessage = null)
            }
        }
    }

    private fun verifyPin() {
        val currentModel = mutableModel.value
        val userId = currentModel.userId ?: return

        viewModelScope.launch {
            mutableModel.update { it.copy(isLoading = true) }

            try {
                val user = userRepository.getUserById(userId) ?: return@launch
                val isValid = pinConverter.verifyPin(currentModel.pin, user.pinHash, user.pinSalt)

                if (isValid) {
                    mutableModel.update {
                        it.copy(
                            isLoading = false,
                            authenticationSuccessful = true,
                            failedAttempts = 0
                        )
                    }

                    userRepository.saveLastLoggedInUser(userId)
                    mutableModel.update {
                        it.copy(
                            authenticationSuccessful = true,
                        )
                    }
                } else {
                    val newFailedAttempts = currentModel.failedAttempts + 1
                    val isLocked = newFailedAttempts >= currentModel.maxAttempts

                    mutableModel.update {
                        it.copy(
                            isLoading = false,
                            pin = "",
                            failedAttempts = newFailedAttempts,
                            showFailedAttemptsMessage = true,
                            isError = true,
                            errorMessage = "Incorrect PIN. Attempts: $newFailedAttempts/${it.maxAttempts}",
                            isLocked = isLocked,
                            lockoutTimeRemainingSeconds = if (isLocked) {
                                val securitySettings = userRepository.getUserSecuritySettings(userId)
                                securitySettings?.lockoutDurationSeconds ?: 30
                            } else 0
                        )
                    }

                    if (isLocked) {
                        startLockoutTimer()
                    }

                    // Auto-dismiss error message after a delay
                    dismissErrorMessageAfterDelay()
                }

            } catch (e: Exception) {
                mutableModel.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = "Authentication error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun startLockoutTimer() {
        viewModelScope.launch {
            var remainingSeconds = mutableModel.value.lockoutTimeRemainingSeconds

            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--

                mutableModel.update {
                    it.copy(
                        lockoutTimeRemainingSeconds = remainingSeconds,
                        errorMessage = "Account locked. Try again in ${remainingSeconds}s"
                    )
                }
            }

            mutableModel.update {
                it.copy(
                    isLocked = false,
                    lockoutTimeRemainingSeconds = 0,
                    failedAttempts = 0,
                    errorMessage = null,
                    isError = false
                )
            }
        }
    }

    private fun dismissErrorMessageAfterDelay(delayMillis: Long = 3000) {
        bannerDismissJob?.cancel()

        bannerDismissJob = viewModelScope.launch {
            delay(delayMillis)
            mutableModel.update {
                it.copy(
                    showFailedAttemptsMessage = false,
                    errorMessage = if (it.isLocked) it.errorMessage else null,
                    isError = it.isLocked
                )
            }
        }
    }

    fun onClearSession() {
        userRepository.clearLastLoggedInUser()
    }
}