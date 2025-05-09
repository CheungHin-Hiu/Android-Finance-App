package com.example.androidfinanceapp.data

import android.content.Context
import com.example.androidfinanceapp.data.database.AssetTotalDatabase
import com.example.androidfinanceapp.data.database.AssetTotalRepository
import com.example.androidfinanceapp.data.database.OfflineAssetTotalRepository
import com.example.androidfinanceapp.network.AssetAPiService
import com.example.androidfinanceapp.network.AuthApiService
import com.example.androidfinanceapp.network.TargetApiService
import com.example.androidfinanceapp.network.TransactionApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

// Dependency Injection container at the application level
interface AppContainer {
    val authRepository: AuthRepository
    val transactionRepository: TransactionRepository
    val assetRepository: AssetRepository
    val targetRepository: TargetRepository
    val assetTotalRepository: AssetTotalRepository
}

// Implementation of the dependency injection container
class DefaultAppContainer(private val context: Context): AppContainer {
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

    private val targetService: TargetApiService by lazy {
        retrofit.create(TargetApiService::class.java)
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

    override val targetRepository: TargetRepository by lazy {
        NetworkTargetRepository(targetService)
    }

    override val assetTotalRepository: AssetTotalRepository by lazy {
        OfflineAssetTotalRepository(AssetTotalDatabase.getDatabase(context = context).assetTotalDao())
    }
}