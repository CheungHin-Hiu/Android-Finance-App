package com.example.androidfinanceapp

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.androidfinanceapp.ui.overview.MonthSelectorTabs
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MonthSelectorTabsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun monthSelectorTabs_DisplaysCorrectMonths() {
        // Arrange
        val currentDate = LocalDate.now()
        val monthNames = listOf(
            currentDate.minusMonths(2).format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            currentDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        )

        // Act
        composeTestRule.setContent {
            MonthSelectorTabs(
                selectedMonth = 2,
                onMonthSelected = {},
                modifier = Modifier.Companion.testTag("monthSelector")
            )
        }

        // Assert - Check each month name is displayed
        for (monthName in monthNames) {
            composeTestRule.onNodeWithText(monthName).assertExists()
        }
    }

    @Test
    fun monthSelectorTabs_ClickingTabCallsOnMonthSelected() {
        // Arrange
        var selectedTab = 2

        // Act
        composeTestRule.setContent {
            MonthSelectorTabs(
                selectedMonth = selectedTab,
                onMonthSelected = { newTab ->
                    selectedTab = newTab
                },
                modifier = Modifier.Companion.testTag("monthSelector")
            )
        }

        // Get month names
        val currentDate = LocalDate.now()
        val previousMonthName = currentDate.minusMonths(1)
            .format(DateTimeFormatter.ofPattern("MMMM yyyy"))

        // Click on previous month tab
        composeTestRule.onNodeWithText(previousMonthName).performClick()

        // Assert the selection changed
        assert(selectedTab == 1) { "Expected selectedTab to be 1 but was $selectedTab" }
    }
}