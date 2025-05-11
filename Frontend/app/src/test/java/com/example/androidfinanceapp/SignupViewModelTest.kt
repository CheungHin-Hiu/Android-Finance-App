package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.AuthRepository
import com.example.androidfinanceapp.network.SignupResponse
import com.example.androidfinanceapp.ui.signup.SignupUiState
import com.example.androidfinanceapp.ui.signup.SignupViewModel
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
class SignupViewModelTest {
    private lateinit var viewModel: SignupViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = SignupViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when passwords don't match, should return error state`() {
        val username = "testuser"
        val password = "password123"
        val confirmPassword = "password456"

        viewModel.signup(username, password, confirmPassword)

        assertEquals(SignupUiState.Error("Password doesn't match"), viewModel.signupUiState)
    }

    @Test
    fun `when signup is successful, should return success state`() = runTest {
        val username = "testuser"
        val password = "password123"
        val mockResponse = Response.success(
            SignupResponse(
                userId = "123",
                userName = username,
                token = "token123"
            )
        )
        coEvery { authRepository.signup(username, password) } returns mockResponse

        viewModel.signup(username, password, password)

        assertEquals(SignupUiState.Success, viewModel.signupUiState)
    }


    @Test
    fun `when signup throws exception, should return error state`() = runTest {
        val username = "testuser"
        val password = "password123"
        val exception = Exception("Network error")
        coEvery { authRepository.signup(username, password) } throws exception

        viewModel.signup(username, password, password)

        assertEquals(
            SignupUiState.Error("An error occurred: Network error"),
            viewModel.signupUiState
        )
    }

    @Test
    fun `setUiStateIdle should set state to Idle`() {
        viewModel.signup("user", "pass", "wrong")

        viewModel.setUiStateIdle()

        assertEquals(SignupUiState.Idle, viewModel.signupUiState)
    }
}