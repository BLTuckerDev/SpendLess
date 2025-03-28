package dev.bltucker.spendless.authentication

data class AuthenticationScreenModel(
    val userId: Long? = null,
    val username: String? = null,
    val pin: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val failedAttempts: Int = 0,
    val maxAttempts: Int = 3,
    val showFailedAttemptsMessage: Boolean = false,
    val isLocked: Boolean = false,
    val lockoutTimeRemainingSeconds: Int = 0,
    val authenticationSuccessful: Boolean = false
)