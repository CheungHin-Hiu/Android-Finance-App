package com.example.androidfinanceapp.ui.signup

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
import kotlinx.coroutines.launch


sealed interface SignupUiState {
    object Idle : SignupUiState
    object Success: SignupUiState
    data class Error(val message: String): SignupUiState
}

class SignupViewModel(private val authRepository: AuthRepository): ViewModel() {
    var signupUiState: SignupUiState by mutableStateOf(SignupUiState.Idle)
        private set

    fun signup(username: String, password: String, confirmPassword: String) {
        if(password != confirmPassword) {
            signupUiState = SignupUiState.Error("Password doesn't match")
            return
        }
        viewModelScope.launch {
            try {
                val response = authRepository.signup(username, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        signupUiState = SignupUiState.Success
                    }
                } else {
                    signupUiState = SignupUiState.Error("Sign up failed: ${response.message()}" )
                }
            } catch (e: Exception) {
                signupUiState = SignupUiState.Error("An error occurred: ${e.message}")
                Log.e("Login error", "" + e.message)
            }
        }

    }

    fun setUiStateIdle() {
        signupUiState = SignupUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val authRepository = application.container.authRepository
                SignupViewModel(authRepository = authRepository)
            }
        }
    }
}