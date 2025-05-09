package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AssetAPiService
import com.example.androidfinanceapp.network.CreateAssetRequest
import com.example.androidfinanceapp.network.GetAssetsResponse
import com.example.androidfinanceapp.network.ModifyAssetRequest
import retrofit2.Response

interface AssetRepository {
    suspend fun getAsset(token: String, currency: String): Response<GetAssetsResponse>

    suspend fun addAsset(token: String, category: String, type: String, amount: Float): Response<Unit>

    suspend fun modifyAsset(token: String, id: Int, amount: Float): Response<Unit>

    suspend fun deleteAsset(token: String, id: Int): Response<Unit>
}

class NetworkAssetRepository(
    private val assetAPiService: AssetAPiService
): AssetRepository {
    override suspend fun getAsset(
        token: String,
        currency: String
    ): Response<GetAssetsResponse> = assetAPiService.getAsset(token = token, currency = currency)

    override suspend fun addAsset(
        token: String,
        category: String,
        type: String,
        amount: Float
    ) = assetAPiService.addAsset(token = token, request = CreateAssetRequest(category, type, amount))

    override suspend fun modifyAsset(
        token: String,
        id: Int,
        amount: Float,
    ) = assetAPiService.modifyAsset(token = token, request = ModifyAssetRequest(id, amount))

    override suspend fun deleteAsset(
        token: String,
        id: Int,
    ) = assetAPiService.deleteAsset(token = token, id = id)
}