package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
    val id: String,
    val category: String,
    val type: String,
    val description: String,
    val amount: Float,
    val value: Float,
    val createdAt: String,
    val updatedAt: String,
)

val currentDateTime = LocalDate.now()

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
    val pieChartData: MutableState<Map<String, Float>> = mutableStateOf(emptyMap())
    val exchangeRate: MutableState<Float> = mutableStateOf(1f)


    fun getAsset(username: String, token: String, currency: String) {
        Log.d("Called get asset", "Get asset being called")
        viewModelScope.launch {
            try {
                val response = assetRepository.getAsset(token, currency)
                if (response.isSuccessful) {
                    response.body()?.let {
                        assetStatisticState = AssetStatisticState.SuccessFetching
                        assetList.clear()
                        assetList.addAll(getAssetResponseParsing(it.assets))
                        pieChartData.value = getPieChartData(assetList)
                        val assetTotal = calculateAssetTotal(assetList)
                        if (assetTotalThisMonth.value.isNotNull()) {
                            Log.d("Update executed", "update")
                            Log.d("Original asset", assetTotalThisMonth.toString() )
                            assetTotalRepository.updateAssetTotalThisMonthAndYear(
                                username = username ,
                                year = currentDateTime.year,
                                month = currentDateTime.month.value,
                                value = assetTotal
                            )
                        } else {
                            Log.d("Insert executed", "insert")
                            assetTotalRepository.insertAssetTotal(
                                AssetTotal(
                                    id = 0,
                                    username = username,
                                    year = currentDateTime.year,
                                    month = currentDateTime.month.value,
                                    value = assetTotal,
                                )
                            )
                        }
                        getAssetTotalByYear(username, currentDateTime.year )
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

    fun getAssetTotalByYear(username: String, year: Int) {
        viewModelScope.launch {
            assetTotalStatisticList.value = assetTotalRepository.getAllAssetTotalsForYear(username, year)
            Log.d("Executed get asset by year", "executed")
        }
    }

    fun getAssetTotalOfMonth(username: String) {
        viewModelScope.launch {
            assetTotalThisMonth.value = assetTotalRepository.getAssetTotalForThisMonth(
                username, currentDateTime.year, currentDateTime.month.value)
        }
    }

    fun getCurrencyExchangeRate(currency: String) {
        viewModelScope.launch {
            val response = assetRepository.getConversionRate(currency)
            if (response.isSuccessful) {
                response.body()?.let {
                    exchangeRate.value = it
                }
            } else {
                assetStatisticState = AssetStatisticState.Error("Error in getting asset: ${response.message()}" )
            }
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

private fun getPieChartData(assetList: List<Asset>): Map<String, Float> {
    val result: MutableMap<String, Float> = mutableMapOf()
    assetList.forEach { asset ->
        result[asset.category] = result.getOrDefault(asset.category, 0f) + asset.value
    }
    return result
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

    fun modifyAsset(token: String, id: String, amount: Float, category: String, type: String) {
        viewModelScope.launch {
            try {
                val response = assetRepository.modifyAsset(token, id, amount, category, type)
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

    fun deleteAsset(token: String, id: String) {
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
        Asset(
            id = assetResponse.id,
            category = assetResponse.category,
            type = assetResponse.type,
            description = assetResponse.category + ": " + assetResponse.type,
            amount = assetResponse.amount,
            value = assetResponse.value,
            createdAt = assetResponse.createdAt,
            updatedAt = assetResponse.updatedAt,
        )
    }.toMutableList()
}