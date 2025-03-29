package dev.bltucker.spendless.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.UserSessionManager
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.login.PinConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val pinConverter: PinConverter,
) : ViewModel() {


    private val mutableModel = MutableStateFlow(
        PreferencesScreenModel(
            isLoading = true,
            useBracketsForExpense = false,
            currencySymbol = "$",
            decimalSeparator = ".",
            thousandsSeparator = ",",
            isError = false
        )
    )

    val observableModel: StateFlow<PreferencesScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart(userId: Long?, username: String?, pin: String?){
        if(hasStarted){
            return
        }

        hasStarted = true

        val hasUserId = userId != null
        val hasUsernameAndPin = username != null && pin != null

        if(!hasUserId && !hasUsernameAndPin){
            updateModelWithError()
            return
        }

        if(hasUserId){
            loadUserPreferences(userId)
            return
        }

        loadModelDefaults(username, pin)
    }

    private fun loadModelDefaults(username: String?, pin: String?) {
        mutableModel.update {
            it.copy(
                userId = null,
                username = username,
                pin = pin,
                isLoading = false,
                isError = false,
                useBracketsForExpense = false,
                currencySymbol = "$",
                decimalSeparator = ".",
                thousandsSeparator = ","
            )
        }
    }

    private fun loadUserPreferences(userId: Long?) {
        viewModelScope.launch {
            userId?.let {
                val userPreferences = userRepository.getUserPreferences(userId)

                if (userPreferences == null) {
                    updateModelWithError()
                    return@launch
                }

                mutableModel.update {
                    it.copy(
                        userId = userId,
                        username = null,
                        pin = null,
                        isLoading = false,
                        isError = false,
                        useBracketsForExpense = userPreferences.useBracketsForExpense,
                        currencySymbol = userPreferences.currencySymbol,
                        decimalSeparator = userPreferences.decimalSeparator,
                        thousandsSeparator = userPreferences.thousandsSeparator
                    )
                }

            }
        }
    }

    private fun updateModelWithError() {
        mutableModel.update {
            it.copy(isLoading = false, isError = true)
        }
    }

    fun onUseBracketsChange(useBrackets: Boolean) {
        mutableModel.update { 
            it.copy(useBracketsForExpense = useBrackets)
        }
    }

    fun onCurrencyChange(symbol: String) {
        mutableModel.update {
            it.copy(currencySymbol = symbol)
        }
    }

    fun onDecimalSeparatorChange(decimalSeparator: String) {
        mutableModel.update {
            it.copy(decimalSeparator = decimalSeparator)
        }
    }

    fun onThousandsSeparatorChange(thousandSeparator: String) {
        mutableModel.update {
            it.copy(thousandsSeparator = thousandSeparator)
        }
    }

    fun onStartTrackingClick() {
        //create the user
        //nav to the dashboard
        viewModelScope.launch {
            val username = mutableModel.value.username ?: return@launch
            val userPin = mutableModel.value.pin ?: return@launch
            val (hash, salt) = pinConverter.hashPin(userPin)
            val userId = userRepository.createUser(username, hash, salt)
            userRepository.saveLastLoggedInUser(userId)
            mutableModel.update {
                it.copy(userId = userId, shouldNavToDashboard = true)
            }
        }
    }

    fun onClearShouldReauthenticate(){
        mutableModel.update {
            it.copy(shouldReauthenticate = false)
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {

            val needsToReauth = userRepository.needsReauthentication()

            if(needsToReauth){
                mutableModel.update {
                    it.copy(shouldReauthenticate = true)
                }
                return@launch
            }

            val updatedPreferences = mutableModel.value.getPreferencesEntity() ?: return@launch

            userRepository.updateUserPreferences(updatedPreferences)
        }
    }
}