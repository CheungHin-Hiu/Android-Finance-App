package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidfinanceapp.TopFinanceApplication
import com.example.androidfinanceapp.data.AssetRepository
import com.example.androidfinanceapp.network.GetAssetsResponse
import kotlinx.coroutines.launch

sealed interface AssetState {
    object Loading : AssetState
    object Idle: AssetState
    object SuccessFetching : AssetState
    object SuccessAdding: AssetState
    object SuccessModifying: AssetState
    object SuccessDeleting: AssetState
    data class Error(val message: String): AssetState
}

data class Asset(
    val id: Int,
    val category: String,
    val type: String,
    val description: String,
    val amount: Float,
    val value: Float,
    val createdAt: String,
    val updatedAt: String,
)

class AssetStatisticsViewModel(private val assetRepository: AssetRepository): ViewModel() {
    var assetState: AssetState by mutableStateOf(AssetState.Loading)
        private set

    var assetList: MutableList<Asset> by mutableStateOf(emptyList<Asset>().toMutableList())

    fun getAsset(token: String, year: String, currency: String) {
        if(token.isEmpty()) {
            assetState = AssetState.Error("Token missing")
            return
        }

        viewModelScope.launch {
            try {
                val response = assetRepository.getAsset(token, year, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        assetState = AssetState.SuccessFetching
                    }
                } else {
                    assetState = AssetState.Error("Error in getting asset: ${response.message()}" )
                }
            } catch (e: Exception) {
                assetState = AssetState.Error("An error occurred: ${e.message}")
                Log.e("Asset fetching error", e.message.toString())
            }
        }
    }

    fun addAsset(token: String, category: String, type: String, value: Float) {
        if(token.isEmpty()) {
            assetState = AssetState.Error("Token missing")
            return
        }

        viewModelScope.launch {
            try {
                val response = assetRepository.addAsset(token, category, type, value)
                if(response.isSuccessful) {
                    response.body()?.let {
                        assetState = AssetState.SuccessAdding
                    }
                } else {
                    assetState = AssetState.Error("Error in add asset: ${response.message()}")
                }
            } catch (e: Exception) {
                assetState = AssetState.Error("An error occurred: ${e.message}")
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun modifyAsset(token: String, id: Int, amount: Float) {
        if(token.isEmpty()) {
            assetState = AssetState.Error("Token missing")
            return
        }

        viewModelScope.launch {
            try {
                val response = assetRepository.modifyAsset(token, id, amount)
                if(response.isSuccessful) {
                    response.body()?.let {
                        assetState = AssetState.SuccessModifying
                    }
                } else {
                    assetState = AssetState.Error("Error in add asset: ${response.message()}")
                }
            } catch (e: Exception) {
                assetState = AssetState.Error("An error occurred: ${e.message}")
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun deleteAsset(token: String, id: Int) {
        if(token.isEmpty()) {
            assetState = AssetState.Error("Token missing")
            return
        }

        viewModelScope.launch {
            try {
                val response = assetRepository.deleteAsset(token, id)
                if(response.isSuccessful) {
                    response.body()?.let {
                        assetState = AssetState.SuccessDeleting
                    }
                } else {
                    assetState = AssetState.Error("Error in add asset: ${response.message()}")
                }
            } catch (e: Exception) {
                assetState = AssetState.Error("An error occurred: ${e.message}")
                Log.e("Login error", "" + e.message)
            }
        }
    }

    fun setStateIdle() {
        assetState = AssetState.Idle
    }

    private fun convertAssetResponse(assetResponse: GetAssetsResponse) : List<Asset> {
        assetResponse.assets.forEachIndexed { index, getAssetResponse ->

        }

        return emptyList()
    }

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