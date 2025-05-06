package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AssetAPiService
import com.example.androidfinanceapp.network.AuthApiService
import com.example.androidfinanceapp.network.TransactionApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

// Dependency Injection container at the application level
interface AppContainer {
    val authRepository: AuthRepository
    val transactionRepository: TransactionRepository
    val assetRepository: AssetRepository
}

// Implementation of the dependency injection container
class DefaultAppContainer: AppContainer {
    private val baseUrl = "http://10.0.2.2:8000/"

    private val json = Json { ignoreUnknownKeys = true}
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val transactionService: TransactionApiService by lazy {
        retrofit.create(TransactionApiService::class.java)
    }

    private val assetService: AssetAPiService by lazy {
        retrofit.create(AssetAPiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        NetworkAuthRepository(authService)
    }

    override val transactionRepository: TransactionRepository by lazy {
        NetworkTransactionRepository(transactionService)
    }

    override val assetRepository: AssetRepository by lazy {
        NetworkAssetRepository(assetService)
    }

}