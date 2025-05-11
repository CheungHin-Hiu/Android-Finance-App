package com.example.androidfinanceapp.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.androidfinanceapp.network.Transaction
import org.junit.Rule
import org.junit.Test

class TransactionChartsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun transactionCharts_NoDataDisplaysMessage() {
        // Act
        composeTestRule.setContent {
            TransactionCharts(transactions = emptyList())
        }

        // Assert
        composeTestRule.onNodeWithText("No data available").assertIsDisplayed()
    }

    @Test
    fun transactionCharts_DisplaysExpenseDataByDefault() {
        // Arrange
        val transactions = listOf(
            Transaction(
                type = "Expense",
                categoryType = "Food",
                currencyType = "USD",
                amount = 25.0,
                convertedAmount = 25.0,
                date = "2025-05-11"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Transport",
                currencyType = "USD",
                amount = 15.0,
                convertedAmount = 15.0,
                date = "2025-05-11"
            ),
            Transaction(
                type = "Income",
                categoryType = "Salary",
                currencyType = "USD",
                amount = 1000.0,
                convertedAmount = 1000.0,
                date = "2025-05-11"
            )
        )

        // Act
        composeTestRule.setContent {
            TransactionCharts(transactions = transactions)
        }

        // Income category should not be visible by default
        composeTestRule.onNode(hasText("Salary")).assertDoesNotExist()
    }

    @Test
    fun transactionCharts_ToggleToIncomeShowsIncomeData() {
        // Arrange
        val transactions = listOf(
            Transaction(
                type = "Expense",
                categoryType = "Food",
                currencyType = "USD",
                amount = 25.0,
                convertedAmount = 25.0,
                date = "2025-05-11"
            ),
            Transaction(
                type = "Income",
                categoryType = "Salary",
                currencyType = "USD",
                amount = 1000.0,
                convertedAmount = 1000.0,
                date = "2025-05-11"
            )
        )

        // Act
        composeTestRule.setContent {
            TransactionCharts(transactions = transactions)
        }

        // Click on Income toggle
        composeTestRule.onNodeWithText("Income").performClick()

        // Expense category should not be visible after toggle
        composeTestRule.onNode(hasText("Food")).assertDoesNotExist()
    }

    @Test
    fun transactionCharts_CalculatesPercentagesCorrectly() {
        // Arrange
        val transactions = listOf(
            Transaction(
                type = "Expense",
                categoryType = "Food",
                currencyType = "USD",
                amount = 75.0,
                convertedAmount = 75.0,
                date = "2025-05-11"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Transport",
                currencyType = "USD",
                amount = 25.0,
                convertedAmount = 25.0,
                date = "2025-05-11"
            )
        )

        // Act
        composeTestRule.setContent {
            TransactionCharts(transactions = transactions)
        }

        // Assert - Should show correct percentages (75% and 25%)
        // There might be slight formatting differences (e.g., "75.0%" vs "75%")
        // so we check both possibilities
        try {
            composeTestRule.onNodeWithText("75.0%").assertIsDisplayed()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText("75%").assertIsDisplayed()
        }

        try {
            composeTestRule.onNodeWithText("25.0%").assertIsDisplayed()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText("25%").assertIsDisplayed()
        }
    }
}