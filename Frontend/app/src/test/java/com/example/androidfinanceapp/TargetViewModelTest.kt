package com.example.androidfinanceapp

import com.example.androidfinanceapp.data.TargetRepository
import com.example.androidfinanceapp.network.GetTargetResponse
import com.example.androidfinanceapp.network.TargetData
import com.example.androidfinanceapp.network.Transaction
import com.example.androidfinanceapp.ui.target.Amount
import com.example.androidfinanceapp.ui.target.TargetState
import com.example.androidfinanceapp.ui.target.TargetType
import com.example.androidfinanceapp.ui.target.TargetViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class TargetViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var targetRepository: TargetRepository
    private lateinit var viewModel: TargetViewModel

    @Before
    fun setup() {
        targetRepository = mockk()
        viewModel = TargetViewModel(targetRepository)
    }

    @Test
    fun `getTarget success should update state and targets`() = runTest {
        val mockTargets = listOf(
            TargetData(
                type = "Saving",
                currency = "USD",
                amount = 1000.0,
                convertedCurrency = "USD",
                convertedAmount = 1000.0,
                createdAt = "2023-01-01"
            )
        )
        val mockResponse = GetTargetResponse(targets = mockTargets)
        coEvery {
            targetRepository.getTarget(any(), any())
        } returns Response.success(mockResponse)

        viewModel.getTarget("token", "USD")

        assertEquals(TargetState.SuccessFetching, viewModel.targetState)
        assertEquals(mockTargets, viewModel.targets)
    }

    @Test
    fun `getTarget failure should update error state`() = runTest {
        coEvery {
            targetRepository.getTarget(any(), any())
        } returns Response.error(400, mockk(relaxed = true))

        viewModel.getTarget("token", "USD")

        assertTrue(viewModel.targetState is TargetState.Error)
    }

    @Test
    fun `addTarget success should update state`() = runTest {
        coEvery {
            targetRepository.addTarget(any(), any(), any(), any())
        } returns Response.success(Unit)

        viewModel.addTarget("token", TargetType.Saving, "USD", 1000.0)

        assertEquals(TargetState.SuccessAdding, viewModel.targetState)
    }

    @Test
    fun `deleteTarget success should update state`() = runTest {
        coEvery {
            targetRepository.deleteTarget(any())
        } returns Response.success(Unit)

        viewModel.deleteTarget("token")

        assertEquals(TargetState.SuccessDeleting, viewModel.targetState)
    }

    @Test
    fun `getAmount success should update amounts`() = runTest {
        val mockTransactions = listOf(
            Transaction(
                type = "income",
                categoryType = "Budget",
                currencyType = "USD",
                amount = 1000.0,
                convertedAmount = 1000.0,
                date = "2025-05-01 10:12:12"
            ),
            Transaction(
                type = "expense",
                categoryType = "Budget",
                currencyType = "USD",
                amount = 500.0,
                convertedAmount = 500.0,
                date = "2025-05-01 10:12:12"
            )
        )
        coEvery {
            targetRepository.getAmount(any(), any())
        } returns Response.success(mockTransactions)

        viewModel.getAmount("token", "USD")

        assertEquals(Amount(saving = 0.0, budget = 0.0), viewModel.amounts)
    }

    @Test
    fun `setGetIdle should reset state to Idle`() {
        viewModel.setGetIdle()

        assertEquals(TargetState.Idle, viewModel.targetState)
    }
}

class MainDispatcherRule : TestRule {
    private val testDispatcher = StandardTestDispatcher()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                Dispatchers.setMain(testDispatcher)
                try {
                    base.evaluate()
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }
}
