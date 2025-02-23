package dev.bltucker.spendless.dashboard

import androidx.compose.runtime.internal.isLiveLiteralsEnabled
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

            } catch(ex: Exception){
                mutableModel.update{
                    it.copy(isError = true, isLoading = false)
                }
            }
        }
    }
}