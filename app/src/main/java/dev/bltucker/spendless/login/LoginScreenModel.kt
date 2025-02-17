package dev.bltucker.spendless.login

data class LoginScreenModel(
    val username: String = "",
    val pin: String = "",
    val loginSuccessful: Boolean = false,
)