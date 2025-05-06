package com.example.androidfinanceapp.ui.asset

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidfinanceapp.TopFinanceApplication
import com.example.androidfinanceapp.data.AssetRepository

sealed interface ManageAssetState {
    object Idle : ManageAssetState
    object Success: ManageAssetState
    data class Error(val message: String): ManageAssetState
}

class ManageAssetViewModel(private val assetRepository: AssetRepository): ViewModel() {
    var manageAssetState: ManageAssetState by mutableStateOf(ManageAssetState.Idle)
        private set

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val assetRepository = application.container.assetRepository
                ManageAssetViewModel(assetRepository = assetRepository)
            }
        }
    }
}