package com.example.androidfinanceapp.ui.Overview

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
import com.example.androidfinanceapp.network.TransactionsResponse
import com.example.androidfinanceapp.ui.signup.SignupViewModel
import kotlinx.coroutines.launch

sealed interface GetTransactionState{
    object Idle : GetTransactionState
    data class Success(val transactionsResponse : TransactionsResponse) : GetTransactionState
    data class Error(val message: String):GetTransactionState
}

sealed interface AddTransactionState{
    object Idle : AddTransactionState
    object Success: AddTransactionState
    data class Error(val message: String):AddTransactionState
}


class OverviewViewModel(private val transactionRepository: TransactionRepository): ViewModel(){
    var getTransactionState: GetTransactionState by mutableStateOf(GetTransactionState.Idle)
        private set
    var addTransactionState: AddTransactionState by  mutableStateOf(AddTransactionState.Idle)
        private set

    fun getTransactions(token: String, startDate: String , endDate: String){
        viewModelScope.launch {
            try {
                val response = transactionRepository.getTransactions(token,startDate,endDate)
                if(response.isSuccessful){
                    response.body()?.let {
                        getTransactionState = GetTransactionState.Success(it)
                    }
                }else{
                    getTransactionState = GetTransactionState.Error("Login failed: ${response.message()}" )
                }
            }catch (e: Exception){
                getTransactionState = GetTransactionState.Error("An error occurred: ${e.message}" )
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun addTransaction(
        token: String,
        type: String,
        categoryType: String,
        currencyType: String,
        amount: Double,
        date: String,
        remark: String,
        createdAt: String){
        viewModelScope.launch {
            try {
                val response = transactionRepository.addTransaction(token,type,categoryType,currencyType,amount,date,remark,createdAt)
                if(response.isSuccessful){
                    response.body()?.let {
                        addTransactionState = AddTransactionState.Success
                    }
                }else{
                    addTransactionState = AddTransactionState.Error("Login failed: ${response.message()}" )
                }
            }catch (e: Exception){
                addTransactionState = AddTransactionState.Error("An error occurred: ${e.message}" )
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun setGetIdle(){
        getTransactionState = GetTransactionState.Idle
    }

    fun setAddIdle(){
        addTransactionState = AddTransactionState.Idle
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