package dev.bltucker.spendless.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(){

    private val mutableModel = MutableStateFlow(LoginScreenModel())

    val observableModel: StateFlow<LoginScreenModel> = mutableModel

    fun onUsernameChange(username: String) {
        mutableModel.update {
            it.copy(username = username)
        }
    }

    fun onPinChange(pin: String) {
        mutableModel.update {
            it.copy(pin = pin)
        }
    }

    fun onLoginClick(username: String, pin: String) {

    }
}