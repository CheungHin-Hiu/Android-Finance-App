package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.AssetRepository
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.data.database.AssetTotalRepository
import com.example.androidfinanceapp.network.GetAssetResponse
import com.example.androidfinanceapp.network.GetAssetsResponse
import com.example.androidfinanceapp.ui.asset.AssetState
import com.example.androidfinanceapp.ui.asset.AssetStatisticState
import com.example.androidfinanceapp.ui.asset.AssetStatisticsViewModel
import com.example.androidfinanceapp.ui.asset.AssetViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AssetStatisticsViewModelTest {
    private lateinit var viewModel: AssetStatisticsViewModel
    private lateinit var assetRepository: AssetRepository
    private lateinit var assetTotalRepository: AssetTotalRepository
    private lateinit var dataStoreManager: DataStoreManager
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        assetRepository = mockk()
        assetTotalRepository = mockk()
        dataStoreManager = mockk()
        viewModel = AssetStatisticsViewModel(assetRepository, assetTotalRepository, dataStoreManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAsset success updates state and data correctly`() = runTest {
        val mockResponse = GetAssetsResponse(
            listOf(
                GetAssetResponse(
                    "1", "Stock", "AAPL", 100f, 150f,
                    "2023-01-01", "2023-01-01"
                )
            )
        )
        coEvery {
            assetRepository.getAsset(any(), any())
        } returns Response.success(mockResponse)

        coEvery {
            assetTotalRepository.updateAssetTotalThisMonthAndYear(any(), any(), any(), any())
        } just Runs

        coEvery {
            assetTotalRepository.getAllAssetTotalsForYear(any(), any())
        } returns emptyList()

        viewModel.getAsset("testUser", "token", "USD")
        advanceUntilIdle()

        assertEquals(1, viewModel.assetList.size)
        assertEquals("Stock", viewModel.assetList[0].category)
    }

    @Test
    fun `getAsset error updates state with error message`() = runTest {
        coEvery {
            assetRepository.getAsset(any(), any())
        } throws Exception("Network error")

        viewModel.getAsset("testUser", "token", "USD")
        advanceUntilIdle()

        assertTrue(viewModel.assetStatisticState is AssetStatisticState.Error)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AssetViewModelTest {
    private lateinit var viewModel: AssetViewModel
    private lateinit var assetRepository: AssetRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        assetRepository = mockk()
        viewModel = AssetViewModel(assetRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAsset success updates state and list`() = runTest {
        val mockResponse = GetAssetsResponse(
            listOf(
                GetAssetResponse(
                    "1", "Stock", "AAPL", 100f, 150f,
                    "2023-01-01", "2023-01-01"
                )
            )
        )
        coEvery {
            assetRepository.getAsset(any(), any())
        } returns Response.success(mockResponse)

        viewModel.getAsset("token", "USD")
        advanceUntilIdle()

        assertEquals(AssetState.SuccessFetching, viewModel.assetState)
        assertEquals(1, viewModel.assetList.size)
    }

    @Test
    fun `addAsset success updates state`() = runTest {
        coEvery {
            assetRepository.addAsset(any(), any(), any(), any())
        } returns Response.success(Unit)
        viewModel.addAsset("token", "Stock", "AAPL", 100f)
        advanceUntilIdle()

        assertEquals(AssetState.SuccessAdding, viewModel.assetState)
    }

    @Test
    fun `modifyAsset success updates state`() = runTest {
        coEvery {
            assetRepository.modifyAsset(any(), any(), any(), any(), any())
        } returns Response.success(Unit)

        viewModel.modifyAsset("token", "1", 100f, "Stock", "AAPL")
        advanceUntilIdle()

        assertEquals(AssetState.SuccessModifying, viewModel.assetState)
    }

    @Test
    fun `deleteAsset success updates state`() = runTest {
        coEvery {
            assetRepository.deleteAsset(any(), any())
        } returns Response.success(Unit)

        viewModel.deleteAsset("token", "1")
        advanceUntilIdle()

        assertEquals(AssetState.SuccessDeleting, viewModel.assetState)
    }
}