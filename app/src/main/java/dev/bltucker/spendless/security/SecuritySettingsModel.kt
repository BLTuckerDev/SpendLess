package dev.bltucker.spendless.security


data class SecuritySettingsModel(
    val userId: Long? = null,

    val isLoading: Boolean = false,
    val isError: Boolean = false,

    val sessionExpirationTimeMinutes: Int? = null,
    val lockedOutDurationSeconds: Int? = null,
){

    val SESSION_DURATION_OPTIONS = listOf(5, 15, 30, 60) // In minutes
    val LOCKOUT_DURATION_OPTIONS = listOf(15, 30, 60, 300) // In seconds
}