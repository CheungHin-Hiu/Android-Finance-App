package com.example.androidfinanceapp.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatePickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun datePickerRow_DisplaysSelectedDate() {
        // Arrange
        val testDate = "2025/5/11(Sun)"

        // Act
        composeTestRule.setContent {
            DatePickerRow(
                selectedDate = testDate,
                onDateSelected = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText(testDate).assertIsDisplayed()
    }

    @Test
    fun datePickerRow_UpdatesOnNewDateSelection() {
        // Arrange
        var currentDate = "2025/5/11(Sun)"


        // Update the date and re-compose
        currentDate = "2025/5/12(Mon)"
        composeTestRule.setContent {
            DatePickerRow(
                selectedDate = currentDate,
                onDateSelected = { newDate ->
                    currentDate = newDate
                }
            )
        }

        // Assert
        composeTestRule.onNodeWithText("2025/5/12(Mon)").assertIsDisplayed()
    }
}