package dev.bltucker.spendless.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bltucker.spendless.common.UserSessionManager
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewmodel @Inject constructor(private val userSessionManager: UserSessionManager) : ViewModel(){

    fun onLogout(){
        userSessionManager.clearLastLoggedInUser()
    }
}