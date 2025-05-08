package com.example.androidfinanceapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TargetApiService {
    @GET("/target")
    suspend fun getTarget(
        @Query ("token") token: String
    ): Response<TargetResponse>

    @POST("/target")
    suspend fun addTarget(
        @Body request: NewTarget
    ): Response<Unit>
}

@Serializable
data class TargetResponse(
    @SerialName("targets") val targets: List<Target>
)

@Serializable
data class Target(
    @SerialName("target_type") val type: String,
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Double,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class NewTarget(
    @SerialName("token") val token: String,
    @SerialName("target_type") val type: String,
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Double,
)