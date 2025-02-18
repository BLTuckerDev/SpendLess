package dev.bltucker.spendless.registration.createpin

data class CreatePinScreenModel(
    val username: String = "",
    val initialPin: String = "",
    val confirmationPin: String = "",
    val isConfirmingPin: Boolean = false,
    val errorMessage: String? = null,
    val shouldNavigateToPreferences: Boolean = false,
) {
    fun isInitialPinComplete() = initialPin.length == 5
    fun isConfirmationPinComplete() = confirmationPin.length == 5
    fun doPinsMatch() = initialPin == confirmationPin
}