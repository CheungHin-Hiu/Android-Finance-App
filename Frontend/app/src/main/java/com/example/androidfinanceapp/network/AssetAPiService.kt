package com.example.androidfinanceapp.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AssetAPiService {
    @GET("/asset")
    suspend fun getAsset(@Body request: GetAssetRequest): Response<GetAssetResponse>

    @POST("/asset")
    suspend fun addAsset()

    @PUT("/asset")
    suspend fun modifyAsset()

    @DELETE("/asset")
    suspend fun deleteAsset()
}

@Serializable
data class GetAssetRequest(
    @Header("Authentication") val token: String,
    @SerialName("year") val year: String,
    @SerialName("currency") val currency: String,
)

@Serializable
data class GetAssetResponse(
    @SerialName("id") val id: Int
)