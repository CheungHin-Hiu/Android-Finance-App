package com.example.androidfinanceapp.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class CalculatorTest {

    @Test
    fun `evaluateExpression should handle basic addition`() {
        assertEquals(10.0, evaluateExpression("5+5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle basic subtraction`() {
        assertEquals(5.0, evaluateExpression("10-5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle basic multiplication`() {
        assertEquals(50.0, evaluateExpression("10*5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle multiplication with special character`() {
        assertEquals(50.0, evaluateExpression("10×5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle basic division`() {
        assertEquals(2.0, evaluateExpression("10/5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle division with special character`() {
        assertEquals(2.0, evaluateExpression("10÷5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle multiple operations with correct precedence`() {
        assertEquals(15.0, evaluateExpression("5+5*2"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle negative numbers`() {
        assertEquals(-5.0, evaluateExpression("-5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle decimal numbers`() {
        assertEquals(5.5, evaluateExpression("5.5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle decimal addition`() {
        assertEquals(10.5, evaluateExpression("5.5+5"), 0.001)
    }

    @Test
    fun `evaluateExpression should handle complex expressions`() {
        assertEquals(14.5, evaluateExpression("5+5*2-0.5"), 0.001)
    }

    @Test
    fun `evaluateMultiplyDivide should handle single multiplication`() {
        assertEquals(10.0, evaluateMultiplyDivide("5*2"), 0.001)
    }

    @Test
    fun `evaluateMultiplyDivide should handle single division`() {
        assertEquals(2.5, evaluateMultiplyDivide("5/2"), 0.001)
    }

    @Test
    fun `evaluateMultiplyDivide should handle multiple operations`() {
        assertEquals(5.0, evaluateMultiplyDivide("10/2*1"), 0.001)
    }

    @Test
    fun `evaluateMultiplyDivide should throw exception for division by zero`() {
        assertFailsWith<ArithmeticException> {
            evaluateMultiplyDivide("5/0")
        }
    }

    @Test
    fun `evaluateSimpleExpression should handle single number`() {
        assertEquals(5.0, evaluateSimpleExpression("5"), 0.001)
    }

    @Test
    fun `evaluateSimpleExpression should handle addition and subtraction`() {
        assertEquals(8.0, evaluateSimpleExpression("5+5-2"), 0.001)
    }

    @Test
    fun `evaluateSimpleExpression should handle expressions with multiplication and division`() {
        assertEquals(8.0, evaluateSimpleExpression("5+5/2+0.5"), 0.001)
    }

    @Test
    fun `evaluateSimpleExpression should handle expressions with all operations`() {
        assertEquals(13.5, evaluateSimpleExpression("5+5*2-1.5"), 0.001)
    }

}
