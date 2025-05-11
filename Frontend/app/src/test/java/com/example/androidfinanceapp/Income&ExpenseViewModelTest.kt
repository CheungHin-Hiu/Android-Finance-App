package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.TransactionRepository
import com.example.androidfinanceapp.ui.overview.AddTransactionState
import com.example.androidfinanceapp.ui.overview.IncomeAndExpenseViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class IncomeAndExpenseViewModelTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var viewModel: IncomeAndExpenseViewModel
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        transactionRepository = mockk()
        viewModel = IncomeAndExpenseViewModel(transactionRepository)
    }

    @Test
    fun `addTransaction sets Success state when repository call is successful`() = runTest {
        val token = "testToken"
        val type = "income"
        val categoryType = "salary"
        val currencyType = "USD"
        val amount = 1000.0
        val date = "2023-10-01"
        coEvery { transactionRepository.addTransaction(token, type, categoryType, currencyType, amount, date) } returns Response.success(Unit)

        viewModel.addTransaction(token, type, categoryType, currencyType, amount, date)
        assertEquals(AddTransactionState.Success, viewModel.addTransactionState)
    }

    @Test
    fun `addTransaction sets Error state when repository call fails`() = runTest {
        val token = "testToken"
        val type = "income"
        val categoryType = "salary"
        val currencyType = "USD"
        val amount = 1000.0
        val date = "2023-10-01"
        coEvery { transactionRepository.addTransaction(token, type, categoryType, currencyType, amount, date) } returns Response.error(400, mockk(relaxed = true))


        viewModel.addTransaction(token, type, categoryType, currencyType, amount, date)


        assert(viewModel.addTransactionState is AddTransactionState.Error)
        val errorState = viewModel.addTransactionState as AddTransactionState.Error
        assertEquals("Add transaction failed: Response.error()", errorState.message)
    }

    @Test
    fun `addTransaction sets Error state when an exception is thrown`() = runTest {

        val token = "testToken"
        val type = "income"
        val categoryType = "salary"
        val currencyType = "USD"
        val amount = 1000.0
        val date = "2023-10-01"
        val exceptionMessage = "Network error"
        coEvery { transactionRepository.addTransaction(token, type, categoryType, currencyType, amount, date) } throws Exception(exceptionMessage)

        viewModel.addTransaction(token, type, categoryType, currencyType, amount, date)

        assert(viewModel.addTransactionState is AddTransactionState.Error)
        val errorState = viewModel.addTransactionState as AddTransactionState.Error
        assertEquals("An error occurred: $exceptionMessage", errorState.message)
    }

    @Test
    fun `setAddIdle sets state to Idle`() {
        viewModel.setAddIdle()

        assertEquals(AddTransactionState.Idle, viewModel.addTransactionState)
    }
}