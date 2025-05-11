package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.TransactionRepository
import com.example.androidfinanceapp.network.Transaction
import com.example.androidfinanceapp.ui.overview.GetTransactionState
import com.example.androidfinanceapp.ui.overview.OverviewViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModelTest {


    private lateinit var transactionRepository: TransactionRepository
    private lateinit var viewModel: OverviewViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        transactionRepository = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = OverviewViewModel(transactionRepository)
    }

    @Test
    fun `getTransactions should set Success state when repository returns successful response`() = runTest {
        val mockTransactions = listOf(
            Transaction(type = "Expense", categoryType = "food", currencyType = "USD", amount = 100.0, convertedAmount = 100.0, date = "2023-10-01"),
            Transaction(type = "Income", categoryType = "salary", currencyType = "USD", amount = 1000.0, convertedAmount = 1000.0, date = "2023-10-01")
        )
        coEvery { transactionRepository.getTransactions(any()) } returns Response.success(mockTransactions)

        viewModel.getTransactions("dummy_token")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(GetTransactionState.Success(mockTransactions), viewModel.getTransactionState)
    }


    @Test
    fun `getTransactions should set Error state when repository throws exception`() = runTest {
        coEvery { transactionRepository.getTransactions(any()) } throws Exception("Network error")

        viewModel.getTransactions("dummy_token")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(GetTransactionState.Error("An error occurred: Network error"), viewModel.getTransactionState)
    }

    @Test
    fun `setGetIdle should set state to Idle`() {
        viewModel.setGetIdle()
        assertEquals(GetTransactionState.Idle, viewModel.getTransactionState)
    }
}
