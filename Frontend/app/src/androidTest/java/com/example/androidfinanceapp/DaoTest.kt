package com.example.androidfinanceapp.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidfinanceapp.data.database.AssetTotal
import com.example.androidfinanceapp.data.database.AssetTotalDao
import com.example.androidfinanceapp.data.database.AssetTotalDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetTotalDaoTest {

    private lateinit var database: AssetTotalDatabase
    private lateinit var dao: AssetTotalDao

    @Before
    fun setup() {
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AssetTotalDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.assetTotalDao()
    }

    @Test
    fun insertAssetTotal_andGetByYearAndMonth() = runBlocking {
        // Insert a sample record
        val assetTotal = AssetTotal(username = "testuser", id = 0, year = 2025, month = 5, value = 1000f)
        dao.insertAssetTotal(assetTotal)

        // Fetch the record by year and month
        val fetchedAssetTotal = dao.getAssetTotalForMonthAndYear("testuser", 2025, 5)

        // Assert that the record matches the inserted data
        assertEquals(assetTotal.year, fetchedAssetTotal?.year)
        assertEquals(assetTotal.month, fetchedAssetTotal?.month)
        assertEquals(assetTotal.value, fetchedAssetTotal?.value)
    }

    @Test
    fun updateAssetTotal_andVerifyUpdate() = runBlocking {
        // Insert a sample record
        val assetTotal = AssetTotal(username = "testuser", id = 0, year = 2025, month = 5, value = 1000f)
        dao.insertAssetTotal(assetTotal)

        // Update the total value for the record
        dao.updateAssetTotalForMonthAndYear("testuser",2025, 5, 2000f)

        // Fetch the updated record
        val updatedAssetTotal = dao.getAssetTotalForMonthAndYear("testuser",2025, 5)

        // Assert that the value was updated correctly
        assertEquals(2000f, updatedAssetTotal?.value)
    }


    @Test
    fun getAllAssetTotalsForYear_sortedByMonth() = runBlocking {
        // Insert multiple records for the same year
        val assetTotals = listOf(
            AssetTotal(username = "testuser",id = 0, year = 2025, month = 3, value = 500f),
            AssetTotal(username = "testuser",id = 0, year = 2025, month = 1, value = 300f),
            AssetTotal(username = "testuser",id = 0, year = 2025, month = 2, value = 400f)
        )
        assetTotals.forEach { dao.insertAssetTotal(it) }

        // Fetch all records for the year, sorted by month
        val fetchedAssetTotals = dao.getAllAssetTotalsForYear("testuser",2025)

        // Assert that the records are sorted by month
        assertEquals(3, fetchedAssetTotals.size)
        assertEquals("01", fetchedAssetTotals[0].month)
        assertEquals("02", fetchedAssetTotals[1].month)
        assertEquals("03", fetchedAssetTotals[2].month)
    }

    @Test
    fun insertAssetTotal_withConflictResolution() = runBlocking {
        // Insert an initial record
        val assetTotal = AssetTotal(username = "testuser",id = 1, year = 2025, month = 5, value = 1000f)
        dao.insertAssetTotal(assetTotal)

        // Insert a new record with the same year and month (conflict resolution)
        val updatedAssetTotal = AssetTotal(username = "testuser", id = 1, year = 2025, month = 5, value = 2000f)
        dao.insertAssetTotal(updatedAssetTotal)

        // Fetch the record and verify that the value was updated
        val fetchedAssetTotal = dao.getAssetTotalForMonthAndYear("testuser",2054, 5)
        assertEquals(2000f, fetchedAssetTotal?.value)
    }
}