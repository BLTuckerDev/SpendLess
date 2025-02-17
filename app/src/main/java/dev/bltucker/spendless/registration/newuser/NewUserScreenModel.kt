package dev.bltucker.spendless.registration.newuser

data class NewUserScreenModel(
    val username: String = "",
    val errorMessage: String? = null,
    val canAdvanceToPinCreation: Boolean = false,
){

    fun isUsernameValid(): Boolean {
        return username.length in 3..14 && username.matches(Regex("^[a-zA-Z0-9]+$"))
    }
}