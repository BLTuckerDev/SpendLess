package dev.bltucker.spendless.registration.createpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePinScreenViewModel @Inject constructor() : ViewModel() {

    private val mutableModel = MutableStateFlow(CreatePinScreenModel())
    val observableModel: StateFlow<CreatePinScreenModel> = mutableModel

    private var hasStarted = false

    private var bannerDismissJob: Job? = null

    fun onStart(username: String){
        if(hasStarted){
            return
        }

        hasStarted = true

        mutableModel.update {
            it.copy(username = username)
        }
    }

    fun onInitialPinDigitEntered(digit: String) {
        if (mutableModel.value.initialPin.length < 5) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    initialPin = currentModel.initialPin + digit,
                    errorMessage = null
                )
            }
        }

        if (mutableModel.value.initialPin.length == 5) {
            mutableModel.update { currentModel ->
                currentModel.copy(isConfirmingPin = true)
            }
        }
    }

    fun onConfirmationPinDigitEntered(digit: String) {
        if (mutableModel.value.confirmationPin.length < 5) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    confirmationPin = currentModel.confirmationPin + digit,
                    errorMessage = null
                )
            }
        }

        // Automatically check if PINs match when confirmation is complete
        if (mutableModel.value.confirmationPin.length == 5) {
            validatePins()
        }
    }

    fun onDeleteInitialPinDigit() {
        if (mutableModel.value.initialPin.isNotEmpty()) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    initialPin = currentModel.initialPin.dropLast(1),
                    errorMessage = null
                )
            }
        }
    }

    fun onDeleteConfirmationPinDigit() {
        if (mutableModel.value.confirmationPin.isNotEmpty()) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    confirmationPin = currentModel.confirmationPin.dropLast(1),
                    errorMessage = null
                )
            }
        }
    }

    fun onNavigateBack() {
        if (mutableModel.value.isConfirmingPin) {
            mutableModel.update { currentModel ->
                currentModel.copy(
                    isConfirmingPin = false,
                    confirmationPin = "",
                    initialPin = "",
                    errorMessage = null
                )
            }
        }
    }

    private fun validatePins() {
        val currentModel = mutableModel.value

        if(!currentModel.doPinsMatch()){
            setErrorMessage("PINs don't match. Try again")
            return
        }

        mutableModel.update {
            it.copy(shouldNavigateToPreferences = true)
        }
    }

    fun onHandledNavigation() {
        mutableModel.update{
            it.copy(shouldNavigateToPreferences = false, initialPin = "", confirmationPin = "", isConfirmingPin = false)
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
                it.copy(errorMessage = null, confirmationPin = "")
            }
        }
    }
}