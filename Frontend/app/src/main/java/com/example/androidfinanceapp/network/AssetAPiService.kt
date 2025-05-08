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
import retrofit2.http.Path
import retrofit2.http.Query

interface AssetAPiService {
    @GET("/asset/{token}")
    suspend fun getAsset(
        @Path("token", encoded = true) token: String,
        @Query(value = "currency") currency: String
    ): Response<List<GetAssetsResponse>>

    @POST("/asset/{token}")
    suspend fun addAsset(
        @Path("token", encoded = true) token: String,
        @Body request: CreateAssetRequest
    ): Response<Unit>

    @PUT("/asset/{token}")
    suspend fun modifyAsset(
        @Path("token", encoded = true) token: String,
        @Body request: ModifyAssetRequest
    ): Response<Unit>

    @DELETE("/asset/{token}")
    suspend fun deleteAsset(
        @Path("token", encoded = true) token: String,
        @Query(value = "id") id: Int
    ): Response<Unit>
}

@Serializable
data class GetAssetResponse(
    @SerialName("id") val id: Int,
    @SerialName("category") val category: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Float,
    @SerialName("converted_amount") val value: Float,
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