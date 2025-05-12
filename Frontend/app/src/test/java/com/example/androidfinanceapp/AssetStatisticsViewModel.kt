package com.example.androidfinanceapp.ui.asset

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidfinanceapp.data.AssetRepository
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.data.database.AssetTotal
import com.example.androidfinanceapp.data.database.AssetTotalRepository
import com.example.androidfinanceapp.network.GetAssetResponse
import com.example.androidfinanceapp.network.GetAssetsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class AssetStatisticsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var assetRepository: AssetRepository

    @Mock
    private lateinit var assetTotalRepository: AssetTotalRepository

    @Mock
    private lateinit var dataStoreManager: DataStoreManager

    private lateinit var viewModel: AssetStatisticsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AssetStatisticsViewModel(assetRepository, assetTotalRepository, dataStoreManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAsset success updates asset list and chart data`() = runTest {
        // Arrange
        val testUsername = "testuser"
        val testToken = "test-token"
        val testCurrency = "USD"
        val testAssets = listOf(
            GetAssetResponse(
                id = "123",
                category = "Cash",
                type = "USD",
                amount = 1000f,
                value = 1000f,
                createdAt = "2023-01-01 12:00:00",
                updatedAt = "2023-01-01 12:00:00"
            ),
            GetAssetResponse(
                id = "456",
                category = "Stock",
                type = "AAPL",
                amount = 10f,
                value = 1500f,
                createdAt = "2023-01-02 12:00:00",
                updatedAt = "2023-01-02 12:00:00"
            )
        )
        val testResponse = GetAssetsResponse(assets = testAssets)

        // Set up repository mock
        `when`(assetRepository.getAsset(testToken, testCurrency))
            .thenReturn(Response.success(testResponse))

        // Set up assetTotalRepository mocks
        `when`(assetTotalRepository.getAssetTotalForThisMonth(testUsername, 2025, 5))
            .thenReturn(null)

        // Act
        viewModel.getAsset(testUsername, testToken, testCurrency)

        // Assert
        assertTrue(viewModel.assetStatisticState is AssetStatisticState.SuccessFetching)
        assertEquals(2, viewModel.assetList.size)
        assertEquals("Cash", viewModel.assetList[0].category)
        assertEquals("Stock", viewModel.assetList[1].category)

        // Verify pie chart data was updated
        assertEquals(2, viewModel.pieChartData.value.size)
        assertEquals(1000f, viewModel.pieChartData.value["Cash"])
        assertEquals(1500f, viewModel.pieChartData.value["Stock"])
    }

    @Test
    fun `getCurrencyExchangeRate updates exchange rate`() = runTest {
        // Arrange
        val testCurrency = "EUR"
        val testRate = 0.85f

        `when`(assetRepository.getConversionRate(testCurrency))
            .thenReturn(Response.success(testRate))

        // Act
        viewModel.getCurrencyExchangeRate(testCurrency)

        // Assert
        assertEquals(testRate, viewModel.exchangeRate.value)
    }

    @Test
    fun `getAssetTotalByYear fetches asset totals`() = runTest {
        // Arrange
        val testUsername = "testuser"
        val testYear = 2023
        val testAssetTotals = listOf(
            AssetTotal(1, testUsername, testYear, 1, 1000f),
            AssetTotal(2, testUsername, testYear, 2, 1200f)
        )

        `when`(assetTotalRepository.getAllAssetTotalsForYear(testUsername, testYear))
            .thenReturn(testAssetTotals)

        // Act
        viewModel.getAssetTotalByYear(testUsername, testYear)

        // Assert
        assertEquals(testAssetTotals, viewModel.assetTotalStatisticList.value)
    }

    @Test
    fun `getAssetTotalOfMonth fetches asset total for current month`() = runTest {
        // Arrange
        val testUsername = "testuser"
        val testYear = 2023
        val testMonth = 5
        val testAssetTotal = AssetTotal(1, testUsername, testYear, testMonth, 1000f)

        `when`(assetTotalRepository.getAssetTotalForThisMonth(testUsername, testYear, testMonth))
            .thenReturn(testAssetTotal)

        // Act
        viewModel.getAssetTotalOfMonth(testUsername)

    }

    @Test
    fun `setStateIdle sets state to idle`() {
        // Act
        viewModel.setStateIdle()

        // Assert
        assertTrue(viewModel.assetStatisticState is AssetStatisticState.Idle)
    }
}