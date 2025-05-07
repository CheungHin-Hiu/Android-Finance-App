package com.example.androidfinanceapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KeypadGrid(
    onAmountChanged: (String) -> Unit,
    onOkPressed: () -> Unit,
    key: CategoryItem?
) {
    // State for the amount input
    var amountValue by remember(key) { mutableStateOf("") }

    // Handle button clicks
    fun handleButtonClick(value: String) {
        when (value) {
            "AC" -> {
                amountValue = ""
                onAmountChanged("")
            }
            "←" -> {
                if (amountValue.isNotEmpty()) {
                    amountValue = amountValue.dropLast(1)
                    onAmountChanged(amountValue)
                }
            }
            "OK" -> {
                // Submit the current value
                onOkPressed()
            }
            "." -> {
                // Improved decimal point handling
                if (amountValue.isEmpty()) {
                    // If empty, add "0."
                    amountValue = "0."
                    onAmountChanged(amountValue)
                } else {
                    // Find the last operation character to determine the current number
                    val lastOpIndex = maxOf(
                        amountValue.lastIndexOf('+'),
                        amountValue.lastIndexOf('-'),
                        amountValue.lastIndexOf('*'),
                        amountValue.lastIndexOf('/'),
                        amountValue.lastIndexOf('×'),
                        amountValue.lastIndexOf('÷')
                    )

                    // Get the current number (all characters after the last operation)
                    val currentNumber = if (lastOpIndex >= 0) {
                        amountValue.substring(lastOpIndex + 1)
                    } else {
                        amountValue
                    }

                    // Only add decimal if the current number doesn't already have one
                    if (!currentNumber.contains('.')) {
                        amountValue += "."
                        onAmountChanged(amountValue)
                    }
                }
            }
            "=" -> {
                // Handle calculation using the current expression
                try {
                    // This is a simple approach - in a real app you'd use a proper expression parser
                    val result = evaluateExpression(amountValue)
                    amountValue = result.toString()
                    // If result is a whole number, remove the decimal part
                    if (amountValue.endsWith(".0")) {
                        amountValue = amountValue.substring(0, amountValue.length - 2)
                    }
                    onAmountChanged(amountValue)
                } catch (e: Exception) {
                    // Handle calculation errors - could show an error message
                    // For now, just keep the current value
                }
            }
            else -> {
                // For other buttons (numbers, operations)
                amountValue += value
                onAmountChanged(amountValue)
            }
        }
    }

    // Larger button size
    val buttonSize = 65.dp

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Row 1: 7, 8, 9, ÷, AC
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LargeCalculatorButton(text = "7", size = buttonSize) { handleButtonClick("7") }
            LargeCalculatorButton(text = "8", size = buttonSize) { handleButtonClick("8") }
            LargeCalculatorButton(text = "9", size = buttonSize) { handleButtonClick("9") }
            LargeCalculatorButton(
                text = "÷",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("/") }
            LargeCalculatorButton(
                text = "AC",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("AC") }
        }

        // Row 2: 4, 5, 6, ×, ←
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LargeCalculatorButton(text = "4", size = buttonSize) { handleButtonClick("4") }
            LargeCalculatorButton(text = "5", size = buttonSize) { handleButtonClick("5") }
            LargeCalculatorButton(text = "6", size = buttonSize) { handleButtonClick("6") }
            LargeCalculatorButton(
                text = "×",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("*") }
            LargeCalculatorButton(
                text = "←",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("←") }
        }

        // Row 3: 1, 2, 3, +, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LargeCalculatorButton(text = "1", size = buttonSize) { handleButtonClick("1") }
            LargeCalculatorButton(text = "2", size = buttonSize) { handleButtonClick("2") }
            LargeCalculatorButton(text = "3", size = buttonSize) { handleButtonClick("3") }
            LargeCalculatorButton(
                text = "+",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("+") }
            LargeCalculatorButton(
                text = "=",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("=") }
        }

        // Row 4: 00, 0, ., -, OK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LargeCalculatorButton(text = "00", size = buttonSize) { handleButtonClick("00") }
            LargeCalculatorButton(text = "0", size = buttonSize) { handleButtonClick("0") }
            LargeCalculatorButton(text = ".", size = buttonSize) { handleButtonClick(".") }
            LargeCalculatorButton(
                text = "-",
                backgroundColor = Color(0xFFFF9800),
                size = buttonSize
            ) { handleButtonClick("-") }
            LargeCalculatorButton(
                text = "OK",
                backgroundColor = Color(0xFFFF4081),
                size = buttonSize
            ) { handleButtonClick("OK") }
        }

        // Add bottom spacing if needed
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Helper function to evaluate expressions
fun evaluateExpression(expression: String): Double {
    try {
        // Replace × with * and ÷ with /
        val sanitizedExpression = expression
            .replace("×", "*")
            .replace("÷", "/")

        // Parse and evaluate the expression using a custom implementation
        return evaluateSimpleExpression(sanitizedExpression)
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid expression: $expression")
    }
}

// Custom expression evaluation for basic arithmetic
fun evaluateSimpleExpression(expr: String): Double {
    // Remove spaces
    val expression = expr.replace(" ", "")

    // First handle addition and subtraction
    val addSubtractTokens = expression.split("+", "-").toMutableList()
    val operators = mutableListOf<Char>()

    // Extract operators in order
    for (char in expression) {
        if (char == '+' || char == '-') {
            operators.add(char)
        }
    }

    // Process multiplication and division within each token
    for (i in addSubtractTokens.indices) {
        if (addSubtractTokens[i].contains('*') || addSubtractTokens[i].contains('/')) {
            addSubtractTokens[i] = evaluateMultiplyDivide(addSubtractTokens[i]).toString()
        }
    }

    // Now process addition and subtraction
    var result = addSubtractTokens[0].toDoubleOrNull() ?: 0.0

    for (i in 0 until operators.size) {
        val nextValue = addSubtractTokens[i + 1].toDoubleOrNull() ?: 0.0

        when (operators[i]) {
            '+' -> result += nextValue
            '-' -> result -= nextValue
        }
    }

    return result
}

// Handle multiplication and division operations
fun evaluateMultiplyDivide(expression: String): Double {
    val tokens = expression.split("*", "/").toMutableList()

    // Extract multiplication and division operators
    val operators = mutableListOf<Char>()
    for (char in expression) {
        if (char == '*' || char == '/') {
            operators.add(char)
        }
    }

    // Start with the first number
    var result = tokens[0].toDoubleOrNull() ?: 0.0

    // Apply operations in order
    for (i in 0 until operators.size) {
        val nextValue = tokens[i + 1].toDoubleOrNull() ?: 0.0

        when (operators[i]) {
            '*' -> result *= nextValue
            '/' -> {
                if (nextValue == 0.0) {
                    throw ArithmeticException("Division by zero")
                }
                result /= nextValue
            }
        }
    }

    return result
}

@Composable
fun LargeCalculatorButton(
    text: String,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    size: Dp = 65.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}