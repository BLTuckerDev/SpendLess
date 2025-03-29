package dev.bltucker.spendless.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.repositories.TransactionRepository
import dev.bltucker.spendless.common.repositories.UserRepository
import dev.bltucker.spendless.common.room.SpendLessUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DashboardScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository) : ViewModel(){

    private val mutableModel = MutableStateFlow<DashboardScreenModel>(DashboardScreenModel(isLoading = true))

    val observableModel: StateFlow<DashboardScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart(userId: Long){
        if(hasStarted){
            return
        }

        hasStarted = true

        viewModelScope.launch {
            try{
                val user: SpendLessUser? = userRepository.getUserById(userId)

                if(user == null){
                    mutableModel.update{
                        it.copy(isError = true, isLoading = false)
                    }
                    return@launch
                }

                mutableModel.update{
                    it.copy(user = user, isLoading = false)
                }

                launch {
                    val userPreferences = userRepository.getUserPreferences(userId)
                    mutableModel.update {
                        it.copy(userPreferences = userPreferences)
                    }
                }

                launch{
                    transactionRepository.getTransactionsForUser(userId).collect{ transactions ->
                        mutableModel.update {
                            it.copy(transactions = transactions)
                        }
                    }
                }



            } catch(ex: Exception){
                mutableModel.update{
                    it.copy(isError = true, isLoading = false)
                }
            }
        }
    }

    fun onTransactionClicked(clickedTransactionId: Long) {
        val latestModel = mutableModel.value
        if(latestModel.clickedTransactionId == clickedTransactionId){
            mutableModel.update {
                it.copy(clickedTransactionId = null)
            }
        } else {
            mutableModel.update {
                it.copy(clickedTransactionId = clickedTransactionId)
            }
        }
    }

    fun onShowExportBottomSheet(){
        mutableModel.update {
            it.copy(showExportBottomSheet = true)
        }
    }

    fun onHideExportBottomSheet(){
        mutableModel.update {
            it.copy(showExportBottomSheet = false)
        }
    }

    fun onClearShouldReauthenticate(){
        mutableModel.update {
            it.copy(shouldReauthenticate = false)
        }
    }

    suspend fun onCheckForReAuth(reAuthAction: ReAuthAction): Boolean {
        val needsToReauth =  userRepository.needsReauthentication()

        if(needsToReauth){
            Log.d("DashboardDebug", "Updating Model")
            mutableModel.update {
                it.copy(shouldReauthenticate = true, reAuthAction = reAuthAction)
            }
        }

        return needsToReauth

    }

    fun onConsumeReAuthAction() {
        mutableModel.update {
            it.copy(reAuthAction = null)
        }
    }
}