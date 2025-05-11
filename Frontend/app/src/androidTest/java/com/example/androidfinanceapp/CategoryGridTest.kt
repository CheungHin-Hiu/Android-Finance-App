package com.example.androidfinanceapp.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.androidfinanceapp.R
import org.junit.Rule
import org.junit.Test

class CategoryGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun categoryGrid_DisplaysCategories() {
        // Arrange
        val categories = listOf(
            CategoryItem(1, R.drawable.transport, "Transport"),
            CategoryItem(2, R.drawable.food, "Food & Drink"),
            CategoryItem(3, R.drawable.entertainment, "Entertainment"),
            CategoryItem(4, R.drawable.real_estate, "Rent")
        )

        // Act
        composeTestRule.setContent {
            CategoryGrid(
                categories = categories,
                selectedCategory = null,
                onCategorySelected = {}
            )
        }

        // Assert
        categories.forEach { category ->
            composeTestRule.onNodeWithText(category.name).assertIsDisplayed()
        }
    }

    @Test
    fun categoryGrid_ClickingCategoryCallsCallback() {
        // Arrange
        val categories = listOf(
            CategoryItem(1, R.drawable.transport, "Transport"),
            CategoryItem(2, R.drawable.food, "Food & Drink")
        )
        var selectedCategory: CategoryItem? = null

        // Act
        composeTestRule.setContent {
            CategoryGrid(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )
        }

        // Click on first category
        composeTestRule.onNodeWithText("Transport").performClick()

        // Assert
        assert(selectedCategory?.name == "Transport") { "Expected Transport but was ${selectedCategory?.name}" }
    }

    @Test
    fun categoryGrid_HandlesMoreThanFourCategories() {
        // Arrange
        val categories = listOf(
            CategoryItem(1, R.drawable.transport, "Transport"),
            CategoryItem(2, R.drawable.food, "Food & Drink"),
            CategoryItem(3, R.drawable.entertainment, "Entertainment"),
            CategoryItem(4, R.drawable.real_estate, "Rent"),
            CategoryItem(5, R.drawable.medicine, "Medicine"),
            CategoryItem(6, R.drawable.shopping, "Shopping"),
            CategoryItem(7, R.drawable.networking, "Networking"),
            CategoryItem(8, R.drawable.other, "Other")
        )

        // Act
        composeTestRule.setContent {
            CategoryGrid(
                categories = categories,
                selectedCategory = null,
                onCategorySelected = {}
            )
        }

        // Assert - Check categories in both rows are displayed
        categories.forEach { category ->
            composeTestRule.onNodeWithText(category.name).assertIsDisplayed()
        }
    }
}