package com.example.androidfinanceapp.ui.overview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidfinanceapp.data.TransactionRepository
import com.example.androidfinanceapp.network.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class OverviewViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var viewModel: OverviewViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = OverviewViewModel(transactionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getTransactions success case - should update state to success`() = testDispatcher.runBlockingTest {
        // Arrange
        val mockedTransactions = listOf(
            Transaction(
                type = "expense",
                categoryType = "Food",
                currencyType = "USD",
                amount = 25.0,
                convertedAmount = 25.0,
                date = "2025-05-01"
            )
        )
        val token = "test_token"

        `when`(transactionRepository.getTransactions(token)).thenReturn(
            Response.success(mockedTransactions)
        )

        // Act
        viewModel.getTransactions(token)

        // Assert
        assert(viewModel.getTransactionState is GetTransactionState.Success)
        val successState = viewModel.getTransactionState as GetTransactionState.Success
        assert(successState.transactionsResponse == mockedTransactions)
    }

    @Test
    fun `getTransactions with response error - should update state to error`() = testDispatcher.runBlockingTest {
        // Arrange
        val errorResponse: Response<List<Transaction>> = Response.error(
            404,
            okhttp3.ResponseBody.create(null, "Not found")
        )
        val token = "test_token"

        `when`(transactionRepository.getTransactions(token)).thenReturn(errorResponse)

        // Act
        viewModel.getTransactions(token)

        // Assert
        assert(viewModel.getTransactionState is GetTransactionState.Error)
        val errorState = viewModel.getTransactionState as GetTransactionState.Error
        assert(errorState.message.contains("Load transaction failed"))
    }


    @Test
    fun `setGetIdle should set state to idle`() {

        // Act
        viewModel.setGetIdle()

        // Assert
        assert(viewModel.getTransactionState is GetTransactionState.Idle)
    }
}