package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.runtime.MutableState
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
import co.yml.charts.common.extensions.isNotNull
import com.example.androidfinanceapp.TopFinanceApplication
import com.example.androidfinanceapp.data.AssetRepository
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.data.database.AssetTotal
import com.example.androidfinanceapp.data.database.AssetTotalRepository
import com.example.androidfinanceapp.network.GetAssetResponse
import kotlinx.coroutines.launch
import java.time.LocalDate


sealed interface AssetState {
    object Loading : AssetState
    object Idle: AssetState
    object SuccessFetching : AssetState
    object SuccessAdding: AssetState
    object SuccessModifying: AssetState
    object SuccessDeleting: AssetState
    data class Error(val message: String): AssetState
}

sealed interface AssetStatisticState {
    object Idle: AssetStatisticState
    object SuccessFetching : AssetStatisticState
    data class Error(val message: String): AssetStatisticState
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

class AssetStatisticsViewModel(
    private val assetRepository: AssetRepository,
    private val assetTotalRepository: AssetTotalRepository,
    dataStoreManager: DataStoreManager,
) :ViewModel() {

    val assetTotalStatisticList: MutableState<List<AssetTotal>> = mutableStateOf(emptyList())
    val assetTotalThisMonth: MutableState<AssetTotal?> = mutableStateOf(null)
    var assetStatisticState: AssetStatisticState by mutableStateOf(AssetStatisticState.Idle)
        private set

    var assetList = mutableStateListOf<Asset>()
        private set

    val username = dataStoreManager.usernameFlow.toString()
    val currentDateTime = LocalDate.now()

    init {
        viewModelScope.launch {

            assetTotalStatisticList.value = assetTotalRepository.getAllAssetTotalsForYear(
                username = username,
                year = currentDateTime.year
            )

            assetTotalThisMonth.value = assetTotalRepository.getAssetTotalForThisMonth(
                username = username,
                year = currentDateTime.year,
                month = currentDateTime.month.value
            )
        }
    }


    fun getAsset(token: String, currency: String) {
        viewModelScope.launch {
            try {
                val response = assetRepository.getAsset(token, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        assetStatisticState = AssetStatisticState.SuccessFetching
                        assetList.clear()
                        assetList.addAll(getAssetResponseParsing(it.assets))
                        val assetTotal = calculateAssetTotal(assetList)
                        if (!assetTotalThisMonth.isNotNull() || assetTotal != assetTotalThisMonth.value?.value) {
                            assetTotalRepository.updateAssetTotalThisMonthAndYear(
                                username = username ,
                                year = currentDateTime.year,
                                month = currentDateTime.month.value,
                                value = assetTotal
                            )
                        }
                    }
                } else {
                    assetStatisticState = AssetStatisticState.Error("Error in getting asset: ${response.message()}" )
                }
            } catch (e: Exception) {
                assetStatisticState = AssetStatisticState.Error("An error occurred: ${e.message}")
                Log.e("Asset fetching error", e.message.toString())
            }
        }
    }

    fun getAssetTotalByYear(year: Int) {
        viewModelScope.launch {
            assetTotalStatisticList.value = assetTotalRepository.getAllAssetTotalsForYear(username, year)
            Log.d("Executed get asset by year", "executed")
        }
    }

    private fun calculateAssetTotal(assetList: MutableList<Asset>): Float {
        var total = 0.0f
        assetList.forEach { asset ->
            total += asset.value
        }
        return total
    }

    fun setStateIdle() {
        assetStatisticState = AssetStatisticState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val assetRepository = application.container.assetRepository
                val assetTotalRepository = application.container.assetTotalRepository
                val context = application.applicationContext
                val dataStoreManager = DataStoreManager(context)
                AssetStatisticsViewModel(
                    assetRepository = assetRepository,
                    assetTotalRepository = assetTotalRepository,
                    dataStoreManager = dataStoreManager
                )
            }
        }
    }
}

class AssetViewModel(private val assetRepository: AssetRepository): ViewModel() {
    var assetState: AssetState by mutableStateOf(AssetState.Loading)
        private set

    var assetList = mutableStateListOf<Asset>()
        private set

    fun getAsset(token: String, currency: String) {
        viewModelScope.launch {
            try {
                val response = assetRepository.getAsset(token, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        assetState = AssetState.SuccessFetching
                        assetList.clear()
                        assetList.addAll(getAssetResponseParsing(it.assets))
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TopFinanceApplication)
                val assetRepository = application.container.assetRepository
                AssetViewModel(assetRepository = assetRepository)
            }
        }
    }
}

private fun getAssetResponseParsing(respondedList: List<GetAssetResponse>): MutableList<Asset> {
    return respondedList.map { assetResponse ->
        val descriptionPrefix = when (assetResponse.category) {
            "Cryptocurrency" -> "Coin: "
            "Stock" -> "Share: "
            else -> currencySymbols[assetResponse.type] + ": "
        }

        Asset(
            id = assetResponse.id,
            category = assetResponse.category,
            type = assetResponse.type,
            description = descriptionPrefix + assetResponse.amount,
            amount = assetResponse.amount,
            value = assetResponse.value,
            createdAt = assetResponse.createdAt,
            updatedAt = assetResponse.updatedAt,
        )
    }.toMutableList()
}