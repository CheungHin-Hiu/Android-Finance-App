package com.example.androidfinanceapp.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidfinanceapp.TopFinanceApplication
import com.example.androidfinanceapp.data.AuthRepository
import com.example.androidfinanceapp.network.LoginResponse
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    data class Success(val loginResponse: LoginResponse): LoginUiState
    data class Error(val message: String): LoginUiState
}

class LoginViewModel(private val authRepository: AuthRepository): ViewModel() {
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set


    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(username, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginUiState = LoginUiState.Success(it)
                    }
                } else {
                    loginUiState = LoginUiState.Error("Login failed: ${response.message()}" )
                }
            } catch (e: Exception) {
                loginUiState = LoginUiState.Error("An error occurred: ${e.message}" )
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun setUiStateIdle() {
        loginUiState = LoginUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val authRepository = application.container.authRepository
                LoginViewModel(authRepository = authRepository)
            }
        }
    }
}


