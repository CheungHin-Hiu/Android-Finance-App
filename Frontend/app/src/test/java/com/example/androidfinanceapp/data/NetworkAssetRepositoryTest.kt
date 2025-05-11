package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AssetAPiService
import com.example.androidfinanceapp.network.GetAssetResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class NetworkAssetRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var assetApiService: AssetAPiService
    private lateinit var assetRepository: NetworkAssetRepository
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        assetApiService = retrofit.create(AssetAPiService::class.java)
        assetRepository = NetworkAssetRepository(assetApiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Get asset successful`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
            {
                "assets": [
                    {
                        "id": "123",
                        "category": "Cash",
                        "type": "USD",
                        "amount": 10.5,
                        "converted_amount": 79.2,
                        "created_at": "2025-05-10 12:30:15",
                        "updated_at": "2025-05-10 12:30:15"
                    }
                ]
            }
        """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.getAsset("test_token", "HKD")

        val expectedAsset = GetAssetResponse(
            id = "123",
            category = "Cash",
            type = "USD",
            amount = 10.5f,
            value = 79.2f,
            createdAt = "2025-05-10 12:30:15",
            updatedAt = "2025-05-10 12:30:15"
        )

        assertEquals(200, response.code())
        assertEquals(listOf(expectedAsset), response.body()?.assets)
    }

    @Test
    fun `Get asset failed`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.getAsset("invalid_token", "HKD")

        assertEquals(404, response.code())
        assertEquals(null, response.body())
    }

    @Test
    fun `Get conversion rate successful`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("7.8")
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.getConversionRate("HKD")

        assertEquals(200, response.code())
        assertEquals(7.8f, response.body())
    }

    @Test
    fun `Get conversion rate failed`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.getConversionRate("INVALID")

        assertEquals(500, response.code())
        assertEquals(null, response.body())
    }

    @Test
    fun `Add asset successful`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.addAsset("test_token", "Cash", "USD", 100.0f)

        assertEquals(201, response.code())
        assertEquals(Unit, response.body())
    }

    @Test
    fun `Add asset failed`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.addAsset("test_token", "InvalidCategory", "USD", -50.0f)

        assertEquals(400, response.code())
        assertEquals(null, response.body())
    }

    @Test
    fun `Modify asset successful`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.modifyAsset(
            token = "test_token",
            id = "123",
            amount = 200.0f,
            category = "Savings",
            type = "USD"
        )

        assertEquals(200, response.code())
        assertEquals(Unit, response.body())
    }

    @Test
    fun `Modify asset failed`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.modifyAsset(
            token = "test_token",
            id = "invalid_id",
            amount = 200.0f,
            category = "Savings",
            type = "USD"
        )

        assertEquals(404, response.code())
        assertEquals(null, response.body())
    }

    @Test
    fun `Delete asset successful`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.deleteAsset("test_token", "123")

        assertEquals(204, response.code())
        assertEquals(null, response.body())
    }

    @Test
    fun `Delete asset failed`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        val response = assetRepository.deleteAsset("test_token", "invalid_id")

        assertEquals(404, response.code())
        assertEquals(null, response.body())
    }
}