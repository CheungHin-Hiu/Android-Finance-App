package com.example.androidfinanceapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AssetTotalDao {
    @Query("SELECT * FROM AssetTotal WHERE username = :username AND year = :year ORDER BY month ASC")
    suspend fun getAllAssetTotalsForYear(username: String, year: Int): List<AssetTotal>

    @Query("SELECT * FROM AssetTotal WHERE  username = :username AND year = :year AND month = :month LIMIT 1")
    suspend fun getAssetTotalForMonthAndYear(username: String, year: Int, month: Int): AssetTotal?

    // Update the asset total record with a specific month and year
    @Query("UPDATE AssetTotal SET total_value = :value WHERE username = :username AND year = :year AND month = :month")
    suspend fun updateAssetTotalForMonthAndYear(username: String, year: Int, month: Int, value: Float)

    // Insert a new asset total record (useful for initial data population)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssetTotal(assetTotal: AssetTotal)
}