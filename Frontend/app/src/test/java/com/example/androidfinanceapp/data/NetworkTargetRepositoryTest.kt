import com.example.androidfinanceapp.data.NetworkTargetRepository
import com.example.androidfinanceapp.network.TargetApiService
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

class NetworkTargetRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var targetApiService: TargetApiService
    private lateinit var targetRepository: NetworkTargetRepository
    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        targetApiService = retrofit.create(TargetApiService::class.java)
        targetRepository = NetworkTargetRepository(targetApiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getTarget success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{
                "targets": []
            }""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.getTarget("testToken", "USD")

        assertEquals(200, response.code())
        assertEquals(0, response.body()?.targets?.size)
    }

    @Test
    fun `addTarget success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.addTarget("testToken", "saving", "USD", 100.0)

        assertEquals(201, response.code())
    }

    @Test
    fun `deleteTarget success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.deleteTarget("testToken")

        assertEquals(204, response.code())
    }

    @Test
    fun `getAmount success`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""[]""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.getAmount("testToken", "USD")

        assertEquals(200, response.code())
        assertEquals(0, response.body()?.size)
    }

    @Test
    fun `getTarget failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
            .setBody("""{"error": "Not Found"}""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.getTarget("invalidToken", "USD")

        assertEquals(404, response.code())
        assertEquals("{\"error\": \"Not Found\"}", response.errorBody()?.string())
    }

    @Test
    fun `addTarget failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""{"error": "Bad Request"}""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.addTarget("testToken", "invalidType", "USD", -100.0)

        assertEquals(400, response.code())
        assertEquals("{\"error\": \"Bad Request\"}", response.errorBody()?.string())
    }

    @Test
    fun `deleteTarget failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(404)
            .setBody("""{"error": "Not Found"}""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.deleteTarget("invalidToken")

        assertEquals(404, response.code())
        assertEquals("{\"error\": \"Not Found\"}", response.errorBody()?.string())
    }

    @Test
    fun `getAmount failure`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("""{"error": "Internal Server Error"}""")
        mockWebServer.enqueue(mockResponse)

        val response = targetRepository.getAmount("testToken", "USD")

        assertEquals(500, response.code())
        assertEquals("{\"error\": \"Internal Server Error\"}", response.errorBody()?.string())
    }
}