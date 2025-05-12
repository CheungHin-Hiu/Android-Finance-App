package com.example.androidfinanceapp.ui.asset

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ManageAssetTabUtilsTest {

    @Test
    fun `getAmountSuffix returns correct suffix for Coin`() {
        assertEquals(" Coin", getAmountSuffix("Coin"))
    }

    @Test
    fun `getAmountSuffix returns correct suffix for Stock`() {
        assertEquals(" Share", getAmountSuffix("Stock"))
    }

    @Test
    fun `getAmountSuffix returns empty string for other categories`() {
        assertEquals("", getAmountSuffix("Cash"))
        assertEquals("", getAmountSuffix("Other"))
    }

    @Test
    fun `transformDate correctly formats date`() {
        val inputDate = "2023-04-15 14:30:45"
        val expected = "2023-04-15:14:30"
        assertEquals(expected, transformDate(inputDate))
    }

    @Test
    fun `filterAssetList filters by type All`() {
        // Arrange
        val assetList = createTestAssetList()

        // Act
        val result = filterAssetList(assetList, "All", "All")

        // Assert
        assertEquals(3, result.size)
    }

    @Test
    fun `filterAssetList filters by specific type`() {
        // Arrange
        val assetList = createTestAssetList()

        // Act
        val result = filterAssetList(assetList, "Cash", "All")

        // Assert
        assertEquals(1, result.size)
        assertEquals("Cash", result[0].category)
    }

    @Test
    fun `filterAssetList filters by this week`() {
        // Arrange
        val assetList = createTestAssetList()
        val now = LocalDate.now()
        val thisWeekMonday = now.with(java.time.DayOfWeek.MONDAY)

        val recentDate = thisWeekMonday.plusDays(1)
        val recentAsset = Asset(
            id = "789",
            category = "Cash",
            type = "USD",
            description = "Cash: USD",
            amount = 500f,
            value = 500f,
            createdAt = recentDate.atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            updatedAt = recentDate.atTime(10, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        )

        val modifiedList = assetList.toMutableList()
        modifiedList.add(recentAsset)

        // Act
        val result = filterAssetList(modifiedList, "All", "This week")

        // Assert
        assertEquals(1, result.size)
        assertEquals("789", result[0].id)
    }

    private fun createTestAssetList(): MutableList<Asset> {
        return mutableListOf(
            Asset(
                id = "123",
                category = "Cash",
                type = "USD",
                description = "Cash: USD",
                amount = 1000f,
                value = 1000f,
                createdAt = "2023-01-01 12:00:00",
                updatedAt = "2023-01-01 12:00:00"
            ),
            Asset(
                id = "456",
                category = "Stock",
                type = "AAPL",
                description = "Stock: AAPL",
                amount = 10f,
                value = 1500f,
                createdAt = "2023-02-01 12:00:00",
                updatedAt = "2023-02-01 12:00:00"
            ),
            Asset(
                id = "789",
                category = "Crypto",
                type = "BTC",
                description = "Crypto: BTC",
                amount = 0.5f,
                value = 20000f,
                createdAt = "2023-03-01 12:00:00",
                updatedAt = "2023-03-01 12:00:00"
            )
        )
    }
}