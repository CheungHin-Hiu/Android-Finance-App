package com.example.androidfinanceapp.ui.target

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
import com.example.androidfinanceapp.data.TargetRepository
import com.example.androidfinanceapp.network.TargetResponse
import kotlinx.coroutines.launch

sealed interface GetTargetState{
    object Idle : GetTargetState
    data class Success(val targetResponse : TargetResponse) : GetTargetState
    data class Error(val message: String):GetTargetState
}

class TargetViewModel(private val targetRepository: TargetRepository): ViewModel(){
    var getTargetState: GetTargetState by mutableStateOf(GetTargetState.Idle)
        private set

    fun getTarget(token: String) {
        viewModelScope.launch {
            try {
                val response = targetRepository.getTarget(token)
                if (response.isSuccessful) {
                    response.body()?.let {
                        getTargetState = GetTargetState.Success(it)
                    }
                } else {
                    getTargetState = GetTargetState.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                getTargetState = GetTargetState.Error("An error occurred: ${e.message}")
                Log.e("Error while fetching user target", " " + e.message)
            }
        }
    }

    fun setGetIdle() {
        getTargetState = GetTargetState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val targetRepository = application.container.targetRepository
                TargetViewModel(targetRepository = targetRepository)
            }
        }
    }
}