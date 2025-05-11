package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.TransactionApiService
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

class NetworkTransactionRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var transactionApiService: TransactionApiService
    private lateinit var transactionRepository: NetworkTransactionRepository
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        transactionApiService = retrofit.create(TransactionApiService::class.java)
        transactionRepository = NetworkTransactionRepository(transactionApiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTransactions success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""[]""")
        mockWebServer.enqueue(mockResponse)

        val response = transactionRepository.getTransactions("testToken")

        assertEquals(200, response.code())
        assertEquals(0, response.body()?.size)
    }

    @Test
    fun `addTransaction success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        val response = transactionRepository.addTransaction(
            token = "testToken",
            type = "expense",
            categoryType = "food",
            currencyType = "USD",
            amount = 50.0,
            date = "2023-10-01"
        )

        assertEquals(201, response.code())
    }

    @Test
    fun `getTransactions failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
            .setBody("""{"error": "Not Found"}""")
        mockWebServer.enqueue(mockResponse)

        val response = transactionRepository.getTransactions("invalidToken")

        assertEquals(404, response.code())
        assertEquals("{\"error\": \"Not Found\"}", response.errorBody()?.string())
    }

    @Test
    fun `addTransaction failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""{"error": "Bad Request"}""")
        mockWebServer.enqueue(mockResponse)

        val response = transactionRepository.addTransaction(
            token = "testToken",
            type = "invalidType",
            categoryType = "invalidCategory",
            currencyType = "USD",
            amount = -50.0,
            date = "2023-10-01"
        )

        assertEquals(400, response.code())
        assertEquals("{\"error\": \"Bad Request\"}", response.errorBody()?.string())
    }
}