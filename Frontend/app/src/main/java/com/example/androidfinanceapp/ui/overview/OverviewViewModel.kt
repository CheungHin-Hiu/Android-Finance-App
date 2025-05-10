package com.example.androidfinanceapp.ui.overview

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
import com.example.androidfinanceapp.data.TransactionRepository
import com.example.androidfinanceapp.network.Transaction
import kotlinx.coroutines.launch
import retrofit2.Response

sealed interface GetTransactionState{
    object Idle : GetTransactionState
    data class Success(val transactionsResponse :List<Transaction>) : GetTransactionState
    data class Error(val message: String):GetTransactionState
}


class OverviewViewModel(private val transactionRepository: TransactionRepository): ViewModel(){
    var getTransactionState: GetTransactionState by mutableStateOf(GetTransactionState.Idle)
        private set


    fun getTransactions(token: String){
        viewModelScope.launch {
            try {
                val response = transactionRepository.getTransactions(token)
                if(response.isSuccessful){
                    response.body()?.let {
                        getTransactionState = GetTransactionState.Success(it)
                    }
                }else{
                    getTransactionState = GetTransactionState.Error("Load transaction failed: ${response.message()}, you may add some transactions before loading" )
                }
            }catch (e: Exception){
                getTransactionState = GetTransactionState.Error("An error occurred: ${e.message}" )
                Log.e("Transaction error", "" + e.message)
            }
        }
    }


    fun setGetIdle(){
        getTransactionState = GetTransactionState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val transactionRepository = application.container.transactionRepository
                OverviewViewModel(transactionRepository = transactionRepository)
            }
        }
    }

}