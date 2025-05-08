package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.NewTarget
import com.example.androidfinanceapp.network.TargetApiService
import com.example.androidfinanceapp.network.TargetResponse
import retrofit2.Response

interface TargetRepository {
    suspend fun getTarget(
        token: String,
    ): Response<TargetResponse>

    suspend fun addTarget(
        token: String,
        targetType: String,
        currency: String,
        amount: Double
    ): Response<Unit>
}

class NetworkTargetRepository(
    private val targetApiService: TargetApiService
): TargetRepository {

    override suspend fun getTarget(
        token: String
    ): Response<TargetResponse> =
        targetApiService.getTarget(
            token = token
        )

    override suspend fun addTarget(
        token: String,
        targetType: String,
        currency: String,
        amount: Double
    ): Response<Unit> =
        targetApiService.addTarget(
            request = NewTarget(
                token = token,
                type = targetType,
                currency = currency,
                amount = amount
            )
        )
}