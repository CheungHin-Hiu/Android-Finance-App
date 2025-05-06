package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AssetAPiService
import com.example.androidfinanceapp.network.GetAssetRequest
import com.example.androidfinanceapp.network.GetAssetResponse
import retrofit2.Response

interface AssetRepository {
    suspend fun getAsset(token: String, year: String, currency: String): Response<GetAssetResponse>

    suspend fun addAsset()

    suspend fun modifyAsset()

    suspend fun deleteAsset()
}

class NetworkAssetRepository(
    private val assetAPiService: AssetAPiService
): AssetRepository {
    override suspend fun getAsset(
        token: String,
        year: String,
        currency: String
    ) = assetAPiService.getAsset(GetAssetRequest(token, year, currency))

    override suspend fun addAsset() = assetAPiService.addAsset()

    override suspend fun modifyAsset() = assetAPiService.modifyAsset()

    override suspend fun deleteAsset() = assetAPiService.deleteAsset()
}