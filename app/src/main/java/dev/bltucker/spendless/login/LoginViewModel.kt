package dev.bltucker.spendless.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val pinConverter: PinConverter,

) : ViewModel(){

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

    fun onLoginClick() {
        viewModelScope.launch{
            val latestModel = mutableModel.value
            val username = latestModel.username
            val user = userRepository.getUser(username)

            if(user == null){
                mutableModel.update {
                    it.copy(errorMessage = "Username or PIN is incorrect")
                }
                return@launch
            }

            val pinMatches = pinConverter.verifyPin(latestModel.pin, user.pinHash, user.pinSalt)

            if(!pinMatches){
                mutableModel.update {
                    it.copy(errorMessage = "Username or PIN is incorrect")
                }
                return@launch
            }

            mutableModel.update {
                it.copy(loginSuccessful = true, errorMessage = null)
            }
        }
    }
}