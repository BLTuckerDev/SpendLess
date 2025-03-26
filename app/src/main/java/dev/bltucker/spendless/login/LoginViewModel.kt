package dev.bltucker.spendless.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var bannerDismissJob: Job? = null

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
                setErrorMessage("Username or PIN is incorrect")
                return@launch
            }

            val pinMatches = pinConverter.verifyPin(latestModel.pin, user.pinHash, user.pinSalt)

            if(!pinMatches){
                setErrorMessage("Username or PIN is incorrect")
                return@launch
            }

            userRepository.saveLastLoggedInUser(user.id)

            mutableModel.update {
                it.copy(loginSuccessful = true, loggedInUserId = user.id, errorMessage = null)
            }
        }
    }

    fun handledLoginSuccessful() {
        mutableModel.update {
            it.copy(loginSuccessful = false, loggedInUserId = null, pin = "")
        }
    }

    private fun setErrorMessage(errorMessage: String) {
        bannerDismissJob?.cancel()

        mutableModel.update {
            it.copy(errorMessage = errorMessage)
        }

        bannerDismissJob = viewModelScope.launch {
            delay(2_000)
            mutableModel.update {
                it.copy(errorMessage = null)
            }
        }
    }
}