package com.example.androidfinanceapp.ui.asset

import com.example.androidfinanceapp.data.database.AssetTotal
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetChartUtilsTest {

    @Test
    fun `standardizeNumber with empty list returns empty list`() {
        val emptyList = emptyList<Float>()
        assertEquals(emptyList<Float>(), standardizeNumber(emptyList))
    }

    @Test
    fun `standardizeNumber with single value returns minimum scale value`() {
        val singleValue = listOf(100f)
        val result = standardizeNumber(singleValue)
        assertEquals(1, result.size)
    }

    @Test
    fun `standardizeNumber scales values correctly`() {
        val values = listOf(0f, 50f, 100f)
        val result = standardizeNumber(values)

        assertEquals(3, result.size)
        assertEquals(10f, result[0]) // Min value gets scaled to 10
        assertEquals(55f, result[1]) // Middle value should be in the middle
        assertEquals(100f, result[2]) // Max value gets scaled to 100
    }

    @Test
    fun `standardizeNumber handles negative values`() {
        val values = listOf(-100f, 0f, 100f)
        val result = standardizeNumber(values)

        assertEquals(3, result.size)
        assertEquals(10f, result[0]) // Min value gets scaled to 10
        assertEquals(55f, result[1]) // Middle value should be in the middle
        assertEquals(100f, result[2]) // Max value gets scaled to 100
    }

    @Test
    fun `standardizeNumber handles all same values`() {
        val values = listOf(50f, 50f, 50f)
        val result = standardizeNumber(values)

        // When all values are the same, they should all be scaled to the minimum
        assertEquals(3, result.size)
    }

}