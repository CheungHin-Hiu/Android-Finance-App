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
import retrofit2.http.Query

interface AssetAPiService {
    @GET("/asset")
    suspend fun getAsset(
        @Header("Authorization") token: String,
        @Query("year") year: String,
        @Query("currency") currency: String,
    ): Response<List<GetAssetsResponse>>

    @POST("/asset")
    suspend fun addAsset(
        @Header("Authorization") token: String,
        @Body request: CreateAssetRequest
    ): Response<Unit>

    @PUT("/asset")
    suspend fun modifyAsset(
        @Header("Authorization") token: String,
        @Body request: ModifyAssetRequest
    ): Response<Unit>

    @DELETE("/asset")
    suspend fun deleteAsset(
        @Header("Authorization") token: String,
        @Body request: DeleteAssetRequest
    ): Response<Unit>
}

@Serializable
data class GetAssetResponse(
    @SerialName("id") val id: Int,
    @SerialName("category") val category: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Float,
    @SerialName("value") val value: Float,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class GetAssetsResponse(
    @SerialName("assets") val assets: List<GetAssetResponse>
)

@Serializable
data class CreateAssetRequest(
    @SerialName("category") val category: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Float,
)

@Serializable
data class ModifyAssetRequest(
    @SerialName("id") val assetId: Int,
    @SerialName("amount") val amount: Float,
)

@Serializable
data class DeleteAssetRequest(
    @SerialName("id") val assetId: Int,
)