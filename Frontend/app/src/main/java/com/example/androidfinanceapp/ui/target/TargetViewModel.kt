package com.example.androidfinanceapp.ui.target

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
import com.example.androidfinanceapp.network.TargetData
import com.example.androidfinanceapp.network.Transaction
import kotlinx.coroutines.launch

sealed interface TargetState {
    object Idle : TargetState
    object SuccessFetching : TargetState
    object SuccessAdding : TargetState
    object SuccessDeleting : TargetState
    data class Error(val message: String) : TargetState
}

data class Amount(
    val saving: Double,
    val budget: Double,
)

class TargetViewModel(private val targetRepository: TargetRepository) : ViewModel() {
    var targetState: TargetState by mutableStateOf(TargetState.Idle)
        private set

    var targets = mutableStateListOf<TargetData>()
        private set

    var amounts = Amount(0.0, 0.0)
        private set

    fun setGetIdle() {
        targetState = TargetState.Idle
    }

    suspend fun getTarget(token: String, currency: String) {
        try {
            val response = targetRepository.getTarget(token, currency)
            if (response.isSuccessful) {
                response.body()?.let {
                    targetState = TargetState.SuccessFetching
                    targets.clear()
                    targets.addAll(it.targets)
                }
            } else {
                targetState = TargetState.Error("Error in getting targets")
            }
        } catch (e: Exception) {
            targetState = TargetState.Error("An error occurred: ${e.message}")
        }
    }

    suspend fun addTarget(
        token: String,
        targetType: TargetType,
        currency: String,
        amount: Double,
        getCurrency: String = "USD"
    ) {
        try {
            // convert target type to string
            val targetTypeString = when (targetType) {
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
        }
    }

    suspend fun deleteTarget(token: String) {
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
        }
    }

    fun getAmount(token: String, currency: String) {
        viewModelScope.launch {
            try {
                val response = targetRepository.getAmount(token, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        targetState = TargetState.SuccessFetching
                        amounts = getAmountResponseParsing(it)
                    }
                } else {
                    targetState = TargetState.Error("Error in getting targets")
                }
            } catch (e: Exception) {
                targetState = TargetState.Error("An error occurred: ${e.message}")
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

    private fun getAmountResponseParsing(response: List<Transaction>): Amount {
        var income = 0.0
        var expense = 0.0

        response.forEach { transaction ->
            if (transaction.type == "expense") {
                expense += transaction.convertedAmount
            } else {
                income += transaction.convertedAmount
            }
        }

        return Amount(
            saving = income,
            budget = expense
        )
    }
}