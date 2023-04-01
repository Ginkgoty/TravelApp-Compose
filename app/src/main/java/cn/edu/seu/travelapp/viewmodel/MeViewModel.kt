/**
 * MeViewModel.kt
 *
 * This file is ViewModel of me view
 *
 * @author Li Jiawen
 * @mail nmjbh@qq.com
 */
package cn.edu.seu.travelapp.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.seu.travelapp.api.UserApi
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.model.Token
import cn.edu.seu.travelapp.ui.state.MeContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeViewState(
    val meContentState: MeContentState = MeContentState.INIT,
    val signUpStatus: Int = 0, // 0 - Not sign up, 1 - Sign up success, 2 - Sign up failed, 3 - Input error.
    val signInStatus: Int = 0, // 0 - Not sign in, 1 - Sign in success, 2 - Sign in failed
    val changePwdStatus: Int = 0, // 0 - Not change, 1 - change success, 2 -change failed, 3 - Input error
    val changeUnameStatus: Int = 0, // 0 - Not change, 1 - change success, 2 -change failed, 3 - Input error
)

class MeViewModel(travelAppState: TravelAppState, dataStore: TokenStorage) : ViewModel() {
    private val _uiState = MutableStateFlow(MeViewState())
    val uiState: StateFlow<MeViewState> = _uiState.asStateFlow()

    private val appDataStore = dataStore
    private val appState = travelAppState

//    init {
//        viewModelScope.launch {
//            dataStore.clearToken()
//        }
//    }

    fun updateContentState(newValue: MeContentState) {
        _uiState.update {
            it.copy(
                meContentState = newValue
            )
        }
    }

    var errorMessage: String by mutableStateOf("")
    fun signUp(uname: String, pwd: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            try {
                val t = userApi.signUp(uname, pwd)
                if (t != null) {
                    appDataStore.saveToken(token = t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    updateSignUpStatus(newValue = 1)
                    updateContentState(newValue = MeContentState.ME)
                    appState.checkLogin(t.token)
                }
            } catch (e: Exception) {
                updateSignUpStatus(2)
                snackBarShowingState = true
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun signIn(uname: String, pwd: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            try {
                val t = userApi.signIn(uname, pwd)
                if (t != null) {
                    appDataStore.saveToken(token = t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    updateSignInStatus(newValue = 1)
                    updateContentState(newValue = MeContentState.ME)
                    appState.checkLogin(t.token)
                }
            } catch (e: Exception) {
                updateSignInStatus(newValue = 2)
                snackBarShowingState = true
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun changePwd(pwd: String, token: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            val currentToken = Token(token)
            try {
                val t = userApi.changePwd(pwd = pwd, token = currentToken)
                Log.d("Change Pwd", t.toString())
                if (t != null && t.result) {
                    snackBarShowingState = true
                    updateChangePwdStatus(newValue = 1)
                }
            } catch (e: Exception) {
                snackBarShowingState = true
                updateChangePwdStatus(newValue = 2)
            }
        }
    }

    fun changeUname(uname: String, token: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            val currentToken = Token(token)
            try {
                val t = userApi.changeUname(uname = uname, token = currentToken)
                if (t != null) {
                    appDataStore.saveToken(t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    snackBarShowingState = true
                    updateChangeUnameStatus(newValue = 1)
                }
            } catch (e: Exception) {
                snackBarShowingState = true
                updateChangeUnameStatus(newValue = 2)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            appDataStore.clearToken()
        }
        updateContentState(MeContentState.INIT)
    }

    var snackBarShowingState by mutableStateOf(false)

    fun updateSignUpStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                signUpStatus = newValue
            )
        }
    }

    fun updateSignInStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                signInStatus = newValue
            )
        }
    }

    fun updateChangePwdStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                changePwdStatus = newValue
            )
        }
    }

    fun updateChangeUnameStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                changeUnameStatus = newValue
            )
        }
    }

}