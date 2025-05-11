package com.example.androidfinanceapp.ui.overview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class TransactionItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun transactionItem_DisplaysCorrectDataForExpense() {
        // Arrange & Act
        composeTestRule.setContent {
            TransactionItem(
                type = "Expense",
                categoryType = "Food",
                currencyType = "USD",
                amount = 25.0,
                date = "2025-05-11"
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("2025-05-11").assertIsDisplayed()
        composeTestRule.onNodeWithText("-25.0 USD").assertIsDisplayed()
    }

    @Test
    fun transactionItem_DisplaysCorrectDataForIncome() {
        // Arrange & Act
        composeTestRule.setContent {
            TransactionItem(
                type = "Income",
                categoryType = "Salary",
                currencyType = "USD",
                amount = 1000.0,
                date = "2025-05-11"
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Salary").assertIsDisplayed()
        composeTestRule.onNodeWithText("2025-05-11").assertIsDisplayed()
        composeTestRule.onNodeWithText("+1000.0 USD").assertIsDisplayed()
    }

    @Test
    fun transactionItem_HandlesOtherCategories() {
        // Arrange & Act
        composeTestRule.setContent {
            TransactionItem(
                type = "Expense",
                categoryType = "Other",
                currencyType = "USD",
                amount = 50.0,
                date = "2025-05-11"
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Other").assertIsDisplayed()
    }
}