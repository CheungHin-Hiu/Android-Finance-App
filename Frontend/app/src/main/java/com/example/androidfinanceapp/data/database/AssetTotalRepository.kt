package com.example.androidfinanceapp.data.database

interface AssetTotalRepository {
    suspend fun getAllAssetTotalsForYear(username: String, year: Int): List<AssetTotal>

    suspend fun getAssetTotalForThisMonth(username: String, year: Int, month: Int): AssetTotal?

    suspend fun updateAssetTotalThisMonthAndYear(username: String, year: Int, month: Int, value: Float)

    suspend fun insertAssetTotal(assetTotal: AssetTotal)
}

class OfflineAssetTotalRepository(private val assetTotalDao: AssetTotalDao) : AssetTotalRepository {
    override suspend fun getAllAssetTotalsForYear(username: String, year: Int): List<AssetTotal>
        = assetTotalDao.getAllAssetTotalsForYear(username, year)

    override suspend fun getAssetTotalForThisMonth(username: String, year: Int, month: Int): AssetTotal?
        = assetTotalDao.getAssetTotalForMonthAndYear(username, year, month)

    override suspend fun updateAssetTotalThisMonthAndYear(
        username: String,
        year: Int,
        month: Int,
        value: Float
    ) = assetTotalDao.updateAssetTotalForMonthAndYear(username, year, month, value)

    override suspend fun insertAssetTotal(assetTotal: AssetTotal)
        = assetTotalDao.insertAssetTotal(assetTotal)
}
