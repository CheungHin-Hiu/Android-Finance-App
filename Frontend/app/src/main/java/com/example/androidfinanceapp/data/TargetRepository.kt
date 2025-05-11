package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.GetTargetResponse
import com.example.androidfinanceapp.network.NewTarget
import com.example.androidfinanceapp.network.TargetApiService
import com.example.androidfinanceapp.network.Transaction
import retrofit2.Response

interface TargetRepository {
    suspend fun getTarget(
        token: String,
        currency: String
    ): Response<GetTargetResponse>

    suspend fun addTarget(
        token: String,
        targetType: String,
        currency: String,
        amount: Double
    ): Response<Unit>

    suspend fun deleteTarget(
        token: String
    ): Response<Unit>

    suspend fun getAmount(
        token: String,
        currency: String
    ): Response<List<Transaction>>
}

class NetworkTargetRepository(
    private val targetApiService: TargetApiService
) : TargetRepository {

    override suspend fun getTarget(
        token: String,
        currency: String
    ): Response<GetTargetResponse> =
        targetApiService.getTarget(
            token = token,
            currency = currency
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

    override suspend fun deleteTarget(token: String): Response<Unit> =
        targetApiService.deleteTarget(token = token)

    override suspend fun getAmount(token: String, currency: String): Response<List<Transaction>> =
        targetApiService.getAmount(
            token = token,
            currency = currency
        )

}