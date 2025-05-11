package com.example.androidfinanceapp.data

import com.example.androidfinanceapp.network.AuthApiService
import com.example.androidfinanceapp.network.LoginRequest
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

class NetworkAuthRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authApiService: AuthApiService
    private lateinit var authRepository: NetworkAuthRepository
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        authApiService = retrofit.create(AuthApiService::class.java)
        authRepository = NetworkAuthRepository(authApiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{
            "user_id": "123",
            "username": "test_user",
            "token": "test_token"
        }""")
        mockWebServer.enqueue(mockResponse)

        val response = authRepository.login("test_user", "test_password")

        val request = LoginRequest(
            "test_user",
            "test_password"
        )

        assertEquals(200, response.code())
        assertEquals("123", response.body()?.userId)
        assertEquals(request.username, response.body()?.userName)
        assertEquals("test_token", response.body()?.token)
    }

    @Test
    fun `login failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody("""{"error": "Invalid credentials"}""")
        mockWebServer.enqueue(mockResponse)

        val response = authRepository.login("wrong_user", "wrong_password")

        assertEquals(401, response.code())
        assertEquals("{\"error\": \"Invalid credentials\"}", response.errorBody()?.string())
    }

    @Test
    fun `signup success`() = runBlocking {

        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("""{
            "user_id": "123",
            "username": "new_user",
            "token": "new_token"
        }""")
        mockWebServer.enqueue(mockResponse)

        val response = authRepository.signup("new_user", "new_password")

        assertEquals(201, response.code())
        assertEquals("123", response.body()?.userId)
        assertEquals("new_user", response.body()?.userName)
        assertEquals("new_token", response.body()?.token)
    }

    @Test
    fun `signup failure`() = runBlocking {

        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""{"error": "Username already exists"}""")
        mockWebServer.enqueue(mockResponse)

        val response = authRepository.signup("existing_user", "password")

        assertEquals(400, response.code())
        assertEquals("{\"error\": \"Username already exists\"}", response.errorBody()?.string())
    }
}