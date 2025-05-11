package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.AuthRepository
import com.example.androidfinanceapp.network.LoginResponse
import com.example.androidfinanceapp.ui.login.LoginUiState
import com.example.androidfinanceapp.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success - updates state to Success`() = runTest {
        val username = "testUser"
        val password = "testPass"
        val loginResponse = LoginResponse(
            userId = "123",
            userName = username,
            token = "test-token"
        )
        coEvery {
            authRepository.login(username, password)
        } returns Response.success(loginResponse)

        viewModel.login(username, password)

        assertEquals(LoginUiState.Success(loginResponse), viewModel.loginUiState)
    }

    @Test
    fun `login failure - updates state to Error with message`() = runTest {
        val username = "testUser"
        val password = "testPass"
        val errorMessage = "Invalid credentials"
        coEvery {
            authRepository.login(username, password)
        } returns Response.error(401, mockk(relaxed = true))

        viewModel.login(username, password)

        assert(viewModel.loginUiState is LoginUiState.Error)
        val error = viewModel.loginUiState as LoginUiState.Error
        assert(error.message.contains("Login failed"))
    }

    @Test
    fun `login exception - updates state to Error with exception message`() = runTest {
        val username = "testUser"
        val password = "testPass"
        val exception = Exception("Network error")
        coEvery {
            authRepository.login(username, password)
        } throws exception

        viewModel.login(username, password)

        assert(viewModel.loginUiState is LoginUiState.Error)
        val error = viewModel.loginUiState as LoginUiState.Error
        assert(error.message.contains("Network error"))
    }

    @Test
    fun `setUiStateIdle - sets state to Idle`() = runTest {
        viewModel.login("user", "pass") // Set some state first

        viewModel.setUiStateIdle()

        assertEquals(LoginUiState.Idle, viewModel.loginUiState)
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(LoginUiState.Idle, viewModel.loginUiState)
    }
}