package com.example.androidfinanceapp.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.androidfinanceapp.R
import org.junit.Rule
import org.junit.Test

class KeypadGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun keypadGrid_DisplaysAllButtons() {
        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = {},
                onOkPressed = {},
                key = null
            )
        }

        // Assert - check all numeric buttons are displayed
        for (i in 0..9) {
            composeTestRule.onNodeWithText(i.toString()).assertIsDisplayed()
        }

        // Check operation buttons
        composeTestRule.onNodeWithText("÷").assertIsDisplayed()
        composeTestRule.onNodeWithText("×").assertIsDisplayed()
        composeTestRule.onNodeWithText("+").assertIsDisplayed()
        composeTestRule.onNodeWithText("-").assertIsDisplayed()

        // Check other buttons
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
        composeTestRule.onNodeWithText(".").assertIsDisplayed()
        composeTestRule.onNodeWithText("AC").assertIsDisplayed()
        composeTestRule.onNodeWithText("←").assertIsDisplayed()
        composeTestRule.onNodeWithText("OK").assertIsDisplayed()
    }

    @Test
    fun keypadGrid_NumberButtonUpdatesAmount() {
        // Arrange
        var amount = ""

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    amount = newAmount
                },
                onOkPressed = {},
                key = null
            )
        }

        // Press buttons to form "123"
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()

        // Assert
        assert(amount == "123") { "Expected amount to be '123' but was '$amount'" }
    }

    @Test
    fun keypadGrid_ACButtonClearsAmount() {
        // Arrange
        var amount = "123"

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    amount = newAmount
                },
                onOkPressed = {},
                key = null
            )
        }

        // Press AC button
        composeTestRule.onNodeWithText("AC").performClick()

        // Assert
        assert(amount == "") { "Expected amount to be empty but was '$amount'" }
    }

    @Test
    fun keypadGrid_BackspaceButtonRemovesLastDigit() {
        // Arrange
        var amount = "123"

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    amount = newAmount
                },
                onOkPressed = {},
                key = CategoryItem(1, R.drawable.food, "Food")
            )
        }

        // First add some digits
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()

        // Then press backspace
        composeTestRule.onNodeWithText("←").performClick()

        // Assert
        assert(amount == "12") { "Expected amount to be '12' but was '$amount'" }
    }

    @Test
    fun keypadGrid_OKButtonCallsCallback() {
        // Arrange
        var okPressed = false

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = {},
                onOkPressed = {
                    okPressed = true
                },
                key = null
            )
        }

        // Press OK button
        composeTestRule.onNodeWithText("OK").performClick()

        // Assert
        assert(okPressed) { "Expected OK callback to be called" }
    }

    @Test
    fun keypadGrid_EqualsButtonCalculatesExpression() {
        // Arrange
        var amount = ""

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    amount = newAmount
                },
                onOkPressed = {},
                key = null
            )
        }

        // Enter "5+5"
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("5").performClick()

        // Calculate result
        composeTestRule.onNodeWithText("=").performClick()

        // Assert
        assert(amount == "10") { "Expected amount to be '10' but was '$amount'" }
    }

    @Test
    fun keypadGrid_DecimalPointHandling() {
        // Arrange
        var amount = ""

        // Act
        composeTestRule.setContent {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    amount = newAmount
                },
                onOkPressed = {},
                key = null
            )
        }

        // Test adding decimal to empty input (should add "0.")
        composeTestRule.onNodeWithText(".").performClick()
        assert(amount == "0.") { "Expected amount to be '0.' but was '$amount'" }

        // Clear and test adding decimal after a number
        composeTestRule.onNodeWithText("AC").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText(".").performClick()
        assert(amount == "5.") { "Expected amount to be '5.' but was '$amount'" }

        // Test that multiple decimals in same number are ignored
        composeTestRule.onNodeWithText(".").performClick()
        assert(amount == "5.") { "Expected amount to still be '5.' but was '$amount'" }
    }
}