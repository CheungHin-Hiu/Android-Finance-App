package com.example.androidfinanceapp.ui.overview

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.common.CategoryGrid
import com.example.androidfinanceapp.ui.common.CategoryItem
import com.example.androidfinanceapp.ui.common.DatePickerRow
import com.example.androidfinanceapp.ui.common.KeypadGrid
import com.example.androidfinanceapp.ui.common.ManageScreenTopAppBar
import com.example.androidfinanceapp.ui.common.evaluateExpression


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
            CategoryItem(4, R.drawable.real_estate, "Rent"),
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
            CategoryItem(4, R.drawable.real_estate, "Rent"),
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
                navController.navigate(Screens.OverviewScreen.route)
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
        )
        navController.navigate(Screens.OverviewScreen.route)

    }

    Scaffold(
        topBar = {
            ManageScreenTopAppBar(
            navController = navController,
            firstTabText = "expense",
            secondTabText = "income",
            selectedTab = selectedTab,
            onTabSelected = { newTab ->
                selectedTab = newTab
            })
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
                    amount = "0.0"
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
            // In the IncomeA
            // ndExpenseScreen, modify the Box that contains the calculator
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
                    DatePickerRow(
                        selectedDate = selectedDate,
                        onDateSelected = { newDate ->
                            selectedDate = newDate
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Calculator grid - without passing onDateSelected
                    KeypadGrid(
                        onAmountChanged = { newAmount ->
                            amount = newAmount
                        },
                        onOkPressed = {
                            // Call the function to add transaction when OK is pressed
                            addTransaction()
                        },
                        key = selectedCategory
                    )
                }
            }
        }
    }
}


