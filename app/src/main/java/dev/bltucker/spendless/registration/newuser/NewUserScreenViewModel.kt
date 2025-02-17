package dev.bltucker.spendless.registration.newuser

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
class NewUserScreenViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val mutableModel = MutableStateFlow(NewUserScreenModel())
    val observableModel: StateFlow<NewUserScreenModel> = mutableModel

    fun onUsernameChange(username: String) {
        val trimmedUsername = username.trim()

        mutableModel.update { currentModel ->
            currentModel.copy(
                username = trimmedUsername,
                errorMessage = null
            )
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val currentUsername = mutableModel.value.username

            val user = userRepository.getUser(currentUsername)

            if (user != null) {
                mutableModel.update {
                    it.copy(
                        errorMessage = "This username has been taken already",
                        canAdvanceToPinCreation = false
                    )
                }
                return@launch
            }

            mutableModel.update {
                it.copy(errorMessage = null, canAdvanceToPinCreation = true)
            }
        }
    }

    fun onHandledNavigation() {
        mutableModel.update {
            it.copy(canAdvanceToPinCreation = false)
        }
    }


}