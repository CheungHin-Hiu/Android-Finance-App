package com.example.androidfinanceapp.ui.asset

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidfinanceapp.TopFinanceApplication
import com.example.androidfinanceapp.data.AssetRepository

sealed interface AssetStatisticsState {
    object Loading : AssetStatisticsState
    object Success : AssetStatisticsState
    data class Error(val message: String): ManageAssetState
}

data class Asset(
    var description: String,
    val amount: String,
    val value: String,
    val createdAt: String,
    val updatedAt: String,
)

class AssetStatisticsViewModel(private val assetRepository: AssetRepository): ViewModel() {
    var assetStatisticsState: AssetStatisticsState by mutableStateOf(AssetStatisticsState.Loading)
        private set

    val assetList: MutableList<Asset> = emptyList<Asset>().toMutableList()


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