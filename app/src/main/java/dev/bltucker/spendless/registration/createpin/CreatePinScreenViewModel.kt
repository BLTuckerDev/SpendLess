package dev.bltucker.spendless.registration.createpin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatePinScreenViewModel @Inject constructor() : ViewModel() {

    private val mutableModel = MutableStateFlow(CreatePinScreenModel())
    val observableModel: StateFlow<CreatePinScreenModel> = mutableModel

    private var hasStarted = false

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
            mutableModel.update {
                it.copy(
                    errorMessage = "PINs don't match. Try again",
                    confirmationPin = "",
                )
            }
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
}