package com.example.androidfinanceapp.ui.target

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.androidfinanceapp.network.GetAssetResponse
import com.example.androidfinanceapp.network.GetTargetResponse
import com.example.androidfinanceapp.network.TargetData
import com.example.androidfinanceapp.ui.asset.Asset
import com.example.androidfinanceapp.ui.asset.AssetState
import com.example.androidfinanceapp.ui.asset.currencySymbols
import kotlinx.coroutines.launch

sealed interface TargetState{
    object Loading : TargetState
    object Idle : TargetState
    object SuccessFetching : TargetState
    object SuccessAdding: TargetState
    object SuccessDeleting: TargetState
    data class Error(val message: String):TargetState
}

data class ViewModelTarget(
    val category: String,
    val type: String,
    val currency: String,
    val amount: Float,
    val convertedCurrency: String,
    val convertedAmount: Float
)

class TargetViewModel(private val targetRepository: TargetRepository): ViewModel(){
    var targetState: TargetState by mutableStateOf(TargetState.Idle)
        private set

    var targets = mutableStateListOf<TargetData>()
        private set

    fun setGetIdle() {
        targetState = TargetState.Idle
    }

    fun getTarget(token: String, currency: String) {
        viewModelScope.launch {
            try {
                val response = targetRepository.getTarget(token, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        targetState = TargetState.SuccessFetching
                        targets.clear()
                        targets.addAll(it.targets)
                        Log.d("viewmodel: ", targets.toString())
                    }
                } else {
                    targetState = TargetState.Error("Error in getting targets")
                }
            } catch (e: Exception) {
                targetState = TargetState.Error("An error occurred: ${e.message}")
                Log.e("Error while fetching user target", e.message.toString())
            }
        }
    }

    fun addTarget(token: String, targetType: TargetType, currency: String, amount: Double) {
        viewModelScope.launch {
            try {
                // convert target type to string
                val targetTypeString = when (targetType){
                    TargetType.Budget -> "Budget"
                    TargetType.Saving -> "Saving"
                }

                val response = targetRepository.addTarget(token, targetTypeString, currency, amount)
                if (response.isSuccessful) {
                    response.body()?.let {
                        targetState = TargetState.SuccessAdding
                    }
                } else {
                    targetState = TargetState.Error("Error in add target: ${response.message()}")
                }
            } catch (e: Exception) {
                targetState = TargetState.Error("An error occurred: ${e.message}")
                Log.e("Login error", e.message.toString())
            }
        }
    }

    fun deleteTarget(token: String) {
        viewModelScope.launch {
            try {
                val response = targetRepository.deleteTarget(token)
                if (response.isSuccessful) {
                    response.body()?.let {
                        targetState = TargetState.SuccessDeleting
                    }
                } else {
                    targetState = TargetState.Error("Error in delete target: ${response.message()}")
                }
            } catch (e: Exception) {
                targetState = TargetState.Error("An error occurred: ${e.message}")
                Log.e("Login error", e.message.toString())
            }
        }
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