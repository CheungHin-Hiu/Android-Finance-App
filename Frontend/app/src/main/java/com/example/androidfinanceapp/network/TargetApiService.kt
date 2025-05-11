package com.example.androidfinanceapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TargetApiService {
    @GET("/target/{token}/{currency}")
    suspend fun getTarget(
        @Path("token", encoded = true) token: String,
        @Path("currency", encoded = true) currency: String,
    ): Response<GetTargetResponse>

    @POST("/target")
    suspend fun addTarget(
        @Body request: NewTarget
    ): Response<Unit>

    @DELETE("/target/{token}")
    suspend fun deleteTarget(
        @Path("token", encoded = true) token: String,
    ): Response<Unit>
}

@Serializable
data class GetTargetResponse(
    @SerialName("targets") val targets: List<TargetData>
)

@Serializable
data class TargetData(
    @SerialName("target_type") val type: String,
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Double,
    @SerialName("converted_currency") val convertedCurrency: String,
    @SerialName("converted_amount") val convertedAmount: Double,
    @SerialName("datetime") val createdAt: String
)

@Serializable
data class NewTarget(
    @SerialName("token") val token: String,
    @SerialName("target_type") val type: String,
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Double,
)