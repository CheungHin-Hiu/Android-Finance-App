package com.example.androidfinanceapp.ui.Overview

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import java.util.Calendar

data class CategoryItem(
    val id: Int,
    val icon: Int,
    val name: String
)

@Composable
fun IncomeAndExpenseScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    incomeAndExpenseViewModel: IncomeAndExpenseViewModel = viewModel(
        factory = IncomeAndExpenseViewModel.Factory
    )
) {
    incomeAndExpenseViewModel.setAddIdle()
    val token by dataStoreManager.tokenFlow.collectAsState(initial = null)
    // Track which tab is selected (Expense or Income)
    var selectedTab by remember { mutableStateOf(0) }

    // Track selected category
    var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }

    // Add state for currency and amount
    var selectedCurrency by remember { mutableStateOf("HKD") }
    var amount by remember { mutableStateOf("") }

    // Currency options
    val currencies = listOf("HKD", "USD", "JPY", "CNY")

    // Currency symbols mapping
    val currencySymbols = mapOf(
        "HKD" to "$",
        "USD" to "$",
        "JPY" to "¥",
        "CNY" to "¥"
    )

    // Get current currency symbol
    val currencySymbol = currencySymbols[selectedCurrency] ?: "$"

    val expenseCategories = remember {
        listOf(
            CategoryItem(1, R.drawable.transport, "Transport"),
            CategoryItem(2, R.drawable.food, "Food & Drink"),
            CategoryItem(3, R.drawable.entertainment, "Entertainment"),
            CategoryItem(4, R.drawable.rent, "Rent"),
            CategoryItem(5, R.drawable.medicine, "Medicine"),
            CategoryItem(6, R.drawable.shopping, "Shopping"),
            CategoryItem(7, R.drawable.networking, "Networking"),
            CategoryItem(8, R.drawable.other, "Other")
        )
    }

    val incomeCategories = remember {
        listOf(
            CategoryItem(1, R.drawable.salary, "Salary"),
            CategoryItem(2, R.drawable.dividend, "Dividend"),
            CategoryItem(3, R.drawable.interest, "Interest"),
            CategoryItem(4, R.drawable.rent, "Rent"),
            CategoryItem(5, R.drawable.loyalty, "Loyalty"),
            CategoryItem(6, R.drawable.gift, "Gift"),
            CategoryItem(7, R.drawable.other, "Other"),
        )
    }

    // Get the appropriate category list based on the selected tab
    val categories = if (selectedTab == 0) expenseCategories else incomeCategories

    var selectedDate by remember { mutableStateOf("2025/12/31(Wed)") }


    // For error display
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Observe the transaction state
    val transactionState = incomeAndExpenseViewModel.addTransactionState

    // Show error dialog if needed
    if (showError) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showError = false }
                ) {
                    Text("OK")
                }
            }
        )
    }


    // Handle the transaction state changes
    LaunchedEffect(transactionState) {
        when (transactionState) {
            is AddTransactionState.Success -> {
                // Reset state and navigate back to overview screen
                incomeAndExpenseViewModel.setAddIdle()
                navController.navigateUp()
            }
            is AddTransactionState.Error -> {
                errorMessage = transactionState.message
                showError = true
            }
            AddTransactionState.Idle -> {
                // Nothing to do for idle state
            }
        }
    }

    // Function to handle adding transaction
    fun addTransaction() {
        // Validate inputs
        if (selectedCategory == null) {
            errorMessage = "Please select a category"
            showError = true
            return
        }

        val amountValue = try {
            // If amount contains operations, evaluate it first
            if (amount.contains("+") || amount.contains("-") ||
                amount.contains("*") || amount.contains("/") ||
                amount.contains("×") || amount.contains("÷")) {
                evaluateExpression(amount)
            } else {
                amount.toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            errorMessage = "Invalid amount: ${e.message}"
            showError = true
            return
        }

        if (amountValue <= 0.0) {
            errorMessage = "Amount must be greater than zero"
            showError = true
            return
        }


        // Determine transaction type
        val type = if (selectedTab == 0) "expense" else "income"

        // State variables needed for the date picker

        // Format the date for API - convert from "2025/12/31(Wed)" to "2025-12-31"
        val formattedDate = selectedDate
            .substringBefore("(")
            .replace("/", "-")

        // Current timestamp for createdAt
        val createdAt = java.time.OffsetDateTime.now().toString()

        // Call the ViewModel to add the transaction
        incomeAndExpenseViewModel.addTransaction(
            token = token.toString(),
            type = type,
            categoryType = selectedCategory!!.name,
            currencyType = selectedCurrency,
            amount = amountValue,
            date = formattedDate,
            remark = "", // You could add a remark field if needed
            createdAt = createdAt
        )

    }



    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF8F0FF)) // Light purple background color
            ) {
                // Back button
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                // Expense/Income toggle in center
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp)
                        .width(220.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Expense Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selectedTab == 0) Color(0xFF7C4DFF) else Color.White
                                )
                                .clickable { selectedTab = 0 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Expense",
                                color = if (selectedTab == 0) Color.White else Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Income Tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selectedTab == 1) Color(0xFF7C4DFF) else Color.White
                                )
                                .clickable { selectedTab = 1 },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Income",
                                color = if (selectedTab == 1) Color.White else Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // More options menu
                IconButton(
                    onClick = { /* Handle menu click */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        // Main content with white background
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .padding(innerPadding)
                .background(Color.White)
                .padding(top=16.dp, bottom = 0.dp)
        ) {
            // Category grid
            CategoryGrid(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                },
                modifier = modifier.padding(start = 10.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 3.dp),
                thickness = 2.dp,
                color = Color.Black
            )

            // Amount and currency section - place this right below the HorizontalDivider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), // Reduced vertical padding to move closer to divider
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left section - Amount with currency symbol
                Row(
                    modifier = Modifier
                        .weight(1f),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currencySymbol,
                        fontSize = 22.sp, // Increased font size
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = amount.ifEmpty { "0" },
                        fontSize = 22.sp, // Increased font size
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp) // Increased height
                        .background(Color(0xFFE0E0E0)) // Light gray divider
                )

                // Currency dropdown
                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedCurrency,
                        fontSize = 20.sp, // Increased font size
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Currency",
                        modifier = Modifier.size(28.dp), // Increased icon size
                        tint = Color.Black
                    )
                }

                Box {
                    androidx.compose.material3.DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currencies.forEach { currency ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = {
                                    Text(
                                        text = currency,
                                        fontSize = 18.sp // Larger dropdown text
                                    )
                                },
                                onClick = {
                                    selectedCurrency = currency
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp),
                thickness = 2.dp,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f) // Take all remaining space
                    .background(Color(0xFFF0E6FF)) // Purple background for the entire area
            ) {

                // Calculator content
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 30.dp),
                    verticalArrangement = Arrangement.SpaceBetween

                ) {
                    // State for date picker
                    var showDatePicker by remember { mutableStateOf(false) }

                    // Date picker row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calendar icon with date picker
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { showDatePicker = true }
                        )

                        Spacer(modifier = Modifier.width(175.dp))

                        // Date text
                        Text(
                            text = selectedDate,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Date picker dialog
                    if (showDatePicker) {
                        val context = LocalContext.current
                        val calendar = Calendar.getInstance()

                        val datePickerDialog = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                calendar.set(year, month, day)
                                val dayOfWeek = when(calendar.get(Calendar.DAY_OF_WEEK)) {
                                    Calendar.MONDAY -> "Mon"
                                    Calendar.TUESDAY -> "Tue"
                                    Calendar.WEDNESDAY -> "Wed"
                                    Calendar.THURSDAY -> "Thu"
                                    Calendar.FRIDAY -> "Fri"
                                    Calendar.SATURDAY -> "Sat"
                                    else -> "Sun"
                                }

                                // Format the date
                                val formattedDate = "${year}/${month+1}/${day}($dayOfWeek)"
                                selectedDate = formattedDate
                                // Update the date in the view model if needed
                                // incomeAndExpenseViewModel.setTransactionDate(formattedDate)
                                showDatePicker = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )

                        // Show the dialog
                        LaunchedEffect(showDatePicker) {
                            datePickerDialog.show()
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Calculator grid - without passing onDateSelected
                    KeypadGrid(
                        onAmountChanged = { newAmount ->
                            amount = newAmount
                        },
                        onOkPressed = {
                            // Call the function to add transaction when OK is pressed
                            addTransaction()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<CategoryItem>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // First row with weight-based layout
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (position in 0 until 4) {
                Box(modifier = Modifier.weight(1f)) {
                    if (position < categories.size) {
                        val category = categories[position]
                        CategoryItem(
                            category = category,
                            isSelected = category == selectedCategory,
                            onSelected = onCategorySelected
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second row with same weight-based layout
        if (categories.size > 4) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (position in 0 until 4) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = position + 4
                        if (index < categories.size) {
                            val category = categories[index]
                            CategoryItem(
                                category = category,
                                isSelected = category == selectedCategory,
                                onSelected = onCategorySelected
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CategoryItem(
    category: CategoryItem,
    isSelected: Boolean,
    onSelected: (CategoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Fixed dimensions for consistency
    val itemWidth = 90.dp
    val itemHeight = 100.dp  // Set a fixed height for all items

    Box(
        modifier = modifier
            .width(itemWidth)
            .height(itemHeight)  // Fixed height
            .clickable { onSelected(category) },
        contentAlignment = Alignment.Center
    ) {
        // Background when selected - position it absolutely within the box
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(100.dp)  // Square size
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0F7FA))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF7C4DFF),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        // Content column - same for both states
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            // Icon
            Image(
                painter = painterResource(id = category.icon),
                contentDescription = category.name,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Category name text
            Text(
                text = category.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun KeypadGrid(
    onAmountChanged: (String) -> Unit,
    onOkPressed: () -> Unit
) {
    // State for the amount input
    var amountValue by remember { mutableStateOf("") }

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
private fun evaluateExpression(expression: String): Double {
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
private fun evaluateSimpleExpression(expr: String): Double {
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
private fun evaluateMultiplyDivide(expression: String): Double {
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

