package com.example.androidfinanceapp.ui.Overview

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.network.Transaction
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    overviewViewModel: OverviewViewModel = viewModel(factory = OverviewViewModel.Factory)
) {
    val getTransactionState = overviewViewModel.getTransactionState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val token by dataStoreManager.tokenFlow.collectAsState(initial = null)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //only for test
    val transactions = remember {
        listOf(
            Transaction(
                type = "Expense",
                categoryType = "Food & Drink",
                currencyType = "HKD",
                amount = 40.50,
                localAmount = 40.50,
                date = "2/5/2025",
                remark = "",
                createdAt = "2/5/2025"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Rent",
                currencyType = "USD",
                amount = 5.00,
                localAmount = 50.50,
                date = "2/5/2025",
                remark = "",
                createdAt = "2/5/2025"
            ),
            Transaction(
                type = "Income",
                categoryType = "Salary",
                currencyType = "abc",
                amount = 40.50,
                localAmount = 60.50,
                date = "1/5/2025",
                remark = "",
                createdAt = "3/5/2025"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Networking",
                currencyType = "USD",
                amount = 40.50,
                localAmount = 60.50,
                date = "1/5/2025",
                remark = "",
                createdAt = "3/5/2025"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Medicine",
                currencyType = "HKD",
                amount = 40.50,
                localAmount = 60.50,
                date = "1/5/2025",
                remark = "",
                createdAt = "3/5/2025"
            ),
            Transaction(
                type = "Expense",
                categoryType = "Entertainment",
                currencyType = "HKD",
                amount = 40.50,
                localAmount = 60.50,
                date = "1/5/2025",
                remark = "",
                createdAt = "3/5/2025"
            ),
            Transaction(
                type = "Income",
                categoryType = "Salary",
                currencyType = "USD",
                amount = 40.50,
                localAmount = 60.50,
                date = "1/5/2025",
                remark = "",
                createdAt = "3/5/2025"
            )
            // More transactions can be added here
        )
    }


    // Default month
    var selectedMonth by remember { mutableStateOf(2) }
    LaunchedEffect(key1 = selectedMonth, key2 = token) {
        // Calculate date range based on selected month
        overviewViewModel.setGetIdle()
        val now = LocalDate.now()
        val month = when (selectedMonth) {
            0 -> now.minusMonths(2) // Two months ago
            1 -> now.minusMonths(1) // Last month
            else -> now             // Current month
        }
        val startDate = month.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE)
        val endDate = month.withDayOfMonth(month.lengthOfMonth()).format(DateTimeFormatter.ISO_DATE)

        // Fetch transactions for the selected month
        overviewViewModel.getTransactions(token.toString(), startDate, endDate)
    }
    Scaffold(
        // Remove default content padding from the scaffold
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
            ) {
                // Menu Icon aligned to the left
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }

                // "Overview" title centered exactly in the TopBar
                Text(
                    text = "Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bottom divider line
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    thickness = 1.dp,
                    color = Color(0xFFE6E0F0)
                )
            }
        },
        floatingActionButton = {
            var isPressed by remember { mutableStateOf(false) }
            val elevation by animateDpAsState(
                targetValue = if (isPressed) 2.dp else 6.dp,
                label = "Button elevation"
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = elevation,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)  // Ensure the touch area is circular
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { /* Navigate to income/expense screen */ }
                        )
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = "Add Transaction",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Month selector tabs
            MonthSelectorTabs(
                selectedMonth = selectedMonth,
                onMonthSelected = {
                    selectedMonth = it
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(315.dp)
            ) {
                /*when (getTransactionState) {
                    // When idle, show a loading indicator
                    is GetTransactionState.Idle -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF4F6BED)
                        )
                    }

                    // When successful, display charts
                    is GetTransactionState.Success -> {
                        val transactionData = getTransactionState.transactionsResponse.transactions
                        if (transactionData.isEmpty()) {
                            // Show message when no transactions found
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions found for this period",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            // Display transaction charts
                            TransactionCharts(transactions = transactionData)
                        }
                    }

                    // When error, show error message with retry option
                    is GetTransactionState.Error -> {
                        val errorMessage = getTransactionState.message
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(24.dp)
                                    .widthIn(max = 300.dp)
                            ) {
                                Text(
                                    text = "Error loading chart data",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = errorMessage,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 12.dp)
                                )

                                Button(
                                    onClick = {
                                        // Retry loading transactions
                                        overviewViewModel.setGetIdle()
                                        val now = LocalDate.now()
                                        val month = when (selectedMonth) {
                                            0 -> now.minusMonths(2) // Two months ago
                                            1 -> now.minusMonths(1) // Last month
                                            else -> now             // Current month
                                        }
                                        val startDate = month.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE)
                                        val endDate = month.withDayOfMonth(month.lengthOfMonth()).format(DateTimeFormatter.ISO_DATE)

                                        overviewViewModel.getTransactions(
                                            token.toString(),
                                            startDate,
                                            endDate
                                        )
                                    },
                                    modifier = Modifier.padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4F6BED)
                                    )
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }*/
                // For development with temp data, uncomment this:
                TransactionCharts(transactions = transactions)
            }





            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFE6E0F0)
            )

            // Transaction List - Handling different states
            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (getTransactionState) {
                    // When idle, show a loading indicator
                    is GetTransactionState.Idle -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF4F6BED)
                        )
                    }

                    // When successful, display the transaction list
                    is GetTransactionState.Success -> {
                        val transactions = getTransactionState.transactionsResponse.transactions

                        if (transactions.isEmpty()) {
                            // Show message when no transactions found
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions found for this period",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            // Display transactions in a LazyColumn
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(transactions) { transaction ->
                                    TransactionItem(
                                        type = transaction.type,
                                        categoryType = transaction.categoryType,
                                        currencyType = transaction.currencyType,
                                        amount = transaction.amount,
                                        date = transaction.date,
                                        remark = transaction.remark,
                                        createdAt = transaction.createdAt
                                    )
                                }
                            }
                        }
                    }

                    // When error, show the error message
                    is GetTransactionState.Error -> {
                        val errorMessage = getTransactionState.message
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,

                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(24.dp)
                                    .widthIn(max = 300.dp)
                            ) {
                                Text(
                                    text = "Error loading transactions",
                                    color = Color.Red,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = errorMessage,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 12.dp)
                                )

                                Button(
                                    onClick = {
                                        // Retry loading transactions
                                        overviewViewModel.setGetIdle()
                                        val now = LocalDate.now()
                                        val month = when (selectedMonth) {
                                            0 -> now.minusMonths(2) // Two months ago
                                            1 -> now.minusMonths(1) // Last month
                                            else -> now             // Current month
                                        }
                                        val startDate = month.withDayOfMonth(1).format(DateTimeFormatter.ISO_DATE)
                                        val endDate = month.withDayOfMonth(month.lengthOfMonth()).format(DateTimeFormatter.ISO_DATE)

                                        overviewViewModel.getTransactions(
                                            token.toString(),
                                            startDate,
                                            endDate
                                        )
                                    },
                                    modifier = Modifier.padding(top = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4F6BED)
                                    )
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }*/

                // If using temp data for development, uncomment this:

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            type = transaction.type,
                            categoryType = transaction.categoryType,
                            currencyType = transaction.currencyType,
                            amount = transaction.amount,
                            date = transaction.date,
                            remark = transaction.remark,
                            createdAt = transaction.createdAt
                        )
                    }
                }


            }
        }

    }

@Composable
fun MonthSelectorTabs(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get current date to calculate month names dynamically
    val currentDate = remember { LocalDate.now() }

    // Create a list of the last 3 months (current and 2 previous)
    val months = remember(currentDate) {
        listOf(
            currentDate.minusMonths(2),
            currentDate.minusMonths(1),
            currentDate
        ).map { date ->
            // Format month and year (e.g., "January 2026")
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }
    }

    TabRow(
        selectedTabIndex = selectedMonth,
        modifier = modifier.fillMaxWidth(),
        containerColor = Color(0xFFF8F0FF), // Light purple background from your design
        contentColor = Color(0xFF6200EE), // Purple for selected tab
        indicator = { tabPositions ->
            // Only show indicator for selected tab
            if (selectedMonth < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedMonth]),
                    height = 2.dp,
                    color = Color(0xFF6200EE)
                )
            }
        }
    ) {
        months.forEachIndexed { index, month ->
            Tab(
                selected = selectedMonth == index,
                onClick = { onMonthSelected(index) },
                text = {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedMonth == index)
                            Color(0xFF6200EE) else
                            Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun TransactionItem(
    type: String,
    categoryType: String,
    currencyType: String,
    amount: Double,
    date: String,
    remark: String,
    createdAt: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Pure white background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp), // Increased vertical padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon - already includes the colored circle
            Box(
                modifier = Modifier
                    .size(64.dp), // Significantly larger icon container
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = when {
                        categoryType.contains("Food", ignoreCase = true) -> painterResource(R.drawable.food_circle)
                        categoryType.contains("Rent", ignoreCase = true) -> painterResource(R.drawable.rent_circle)
                        categoryType.contains("Salary", ignoreCase = true) -> painterResource(R.drawable.salary_circle)
                        categoryType.contains("Interest", ignoreCase = true) -> painterResource(R.drawable.food_circle)
                        categoryType.contains("Transport", ignoreCase = true) -> painterResource(R.drawable.transport_circle)
                        categoryType.contains("Shopping", ignoreCase = true) -> painterResource(R.drawable.shopping_circle)
                        categoryType.contains("Medicine", ignoreCase = true) -> painterResource(R.drawable.medicine_circle)
                        categoryType.contains("Entertainment", ignoreCase = true) -> painterResource(R.drawable.entertainment_circle)
                        categoryType.contains("Gift", ignoreCase = true) -> painterResource(R.drawable.gift_circle)
                        categoryType.contains("networking", ignoreCase = true) -> painterResource(R.drawable.networking_circle)
                        else -> painterResource(R.drawable.salary_circle)
                    },
                    contentDescription = categoryType,
                    modifier = Modifier.size(64.dp) // Full-size image
                )
            }

            // Category Name and Date
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = categoryType,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, // Much larger font size
                    color = Color.Black
                )
                Text(
                    text = date,
                    color = Color.Gray,
                    fontSize = 16.sp, // Increased font size for date
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Amount - positive or negative with better formatting
            Text(
                text = if (type.contains("Expense", ignoreCase = true))
                    "-${amount} $currencyType"
                else
                    "+$amount $currencyType",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp, // Much larger font size for amount
                color = if (type.contains("Expense", ignoreCase = true)) Color.Red else Color(0xFF4CAF50),
                textAlign = TextAlign.End
            )
        }

    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TransactionCharts(transactions: List<Transaction>) {
    // Calculate summary data
    val totalIncome = transactions.filter { it.type.contains("Income", ignoreCase = true) }
        .sumOf { it.localAmount }
    val totalExpense = transactions.filter { it.type.contains("Expense", ignoreCase = true) }
        .sumOf { it.localAmount }

    // Group transactions by category
    val expenseByCategory = transactions
        .filter { it.type.contains("Expense", ignoreCase = true) }
        .groupBy { it.categoryType }
        .mapValues { it.value.sumOf { transaction -> transaction.localAmount } }

    val incomeByCategory = transactions
        .filter { it.type.contains("Income", ignoreCase = true) }
        .groupBy { it.categoryType }
        .mapValues { it.value.sumOf { transaction -> transaction.localAmount } }

    // Create pie chart data
    val expenseChartData = expenseByCategory.map { (category, amount) ->
        ChartItem(
            category = category,
            amount = amount,
            percentage = if (totalExpense > 0) (amount / totalExpense * 100) else 0.0,
            color = getCategoryColor(category)
        )
    }

    val incomeChartData = incomeByCategory.map { (category, amount) ->
        ChartItem(
            category = category,
            amount = amount,
            percentage = if (totalIncome > 0) (amount / totalIncome * 100) else 0.0,
            color = getCategoryColor(category)
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Summary information - Total Income and Total Expense
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Income summary
            Column {
                Text(
                    text = "Category",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )

            }


        }

        // Horizontally scrollable charts without cards
        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Expense chart
            item {
                SimpleChartView(
                    title = "Expense",
                    chartData = expenseChartData,
                    totalAmount = totalExpense,
                    isExpense = true
                )
            }

            // Income chart
            item {
                SimpleChartView(
                    title = "Income",
                    chartData = incomeChartData,
                    totalAmount = totalIncome,
                    isExpense = false
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SimpleChartView(
    title: String,
    chartData: List<ChartItem>,
    totalAmount: Double,
    isExpense: Boolean
) {
    Column(
        modifier = Modifier
            .width(300.dp)
    ) {
        // Title
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )

        if (chartData.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data available",
                    color = Color.Gray
                )
            }
        } else {
            // Pie chart with labeled segments
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(
                    data = chartData,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DonutChart(
    data: List<ChartItem>,
    modifier: Modifier = Modifier,

) {
    if (data.isEmpty()) return

    // Track which segment is being hovered
    var hoveredSegment by remember { mutableStateOf<ChartItem?>(null) }

    // Track mouse position for tooltip
    var mousePosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.4f)
            .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.first().position

                            // Save position for tooltip
                            mousePosition = position

                            val canvasCenter = Offset(x = size.width / 2f, y = size.height / 2f)
                            val vector = position - canvasCenter

                            // Calculate distance from center
                            val distanceFromCenter = vector.getDistance()
                            val radius = minOf(size.width, size.height) / 2 * 0.8f
                            val innerRadius = radius * 0.6f

                            // Skip if mouse is in center area or outside the chart
                            if (distanceFromCenter < innerRadius || distanceFromCenter > radius) {
                                hoveredSegment = null
                                continue
                            }

                            // Calculate angle in radians, then convert to degrees
                            val angleRadians = kotlin.math.atan2(vector.y, vector.x)
                            var angleDegrees = Math.toDegrees(angleRadians.toDouble()).toFloat()

                            // Normalize to 0-360 degrees, starting from the top (90 degrees in atan2 terms)
                            angleDegrees = (angleDegrees + 360) % 360

                            // DEBUG CODE - log angleDegrees to see the values
                            // Log.d("DonutChart", "Angle: $angleDegrees")

                            // Find which segment this angle belongs to
                            var startAngle = 0f
                            hoveredSegment = null

                            for (item in data) {
                                val sweepAngle = (item.percentage * 3.6).toFloat()

                                // Handle the case where an angle wraps around 360/0
                                val endAngle = (startAngle + sweepAngle) % 360

                                if (sweepAngle > 0 && (
                                            // Normal case
                                            (startAngle <= angleDegrees && angleDegrees < startAngle + sweepAngle) ||
                                                    // Wrap-around case
                                                    (startAngle <= angleDegrees + 360 && angleDegrees + 360 < startAngle + sweepAngle)
                                            )) {
                                    hoveredSegment = item
                                    break
                                }

                                startAngle += sweepAngle
                            }
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = minOf(canvasWidth, canvasHeight) / 2 * 0.8f
            val center = Offset(x = canvasWidth / 2f, y = canvasHeight / 2f)

            // Draw pie segments - start at 0 degrees (top)
            var startAngle = 0f

            data.forEach { item ->
                val sweepAngle = (item.percentage * 3.6).toFloat()

                if (sweepAngle > 0) {
                    // Determine if segment is hovered
                    val isHovered = item == hoveredSegment
                    val segmentColor = if (isHovered) {
                        item.color.copy(alpha = 1.0f)
                    } else {
                        item.color.copy(alpha = 0.85f)
                    }

                    // Draw segment
                    drawArc(
                        color = segmentColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(x = center.x - radius, y = center.y - radius),
                        size = Size(width = radius * 2, height = radius * 2)
                    )

                    // Draw white outline
                    drawArc(
                        color = Color.White,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        style = Stroke(width = 2f),
                        topLeft = Offset(x = center.x - radius, y = center.y - radius),
                        size = Size(width = radius * 2, height = radius * 2)
                    )

                    // Add segment highlight effect when hovered
                    if (isHovered) {
                        // Calculate midpoint angle for this segment
                        val midAngle = startAngle + (sweepAngle / 2)
                        val midAngleRad = Math.toRadians(midAngle.toDouble())
                        val offsetDistance = 5f // How much to pop out

                        val offsetX = (offsetDistance * cos(midAngleRad)).toFloat()
                        val offsetY = (offsetDistance * sin(midAngleRad)).toFloat()

                        drawArc(
                            color = item.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(
                                x = center.x - radius + offsetX,
                                y = center.y - radius + offsetY
                            ),
                            size = Size(width = radius * 2, height = radius * 2)
                        )
                    }
                }

                startAngle += sweepAngle
            }

            // Draw inner circle for donut effect
            drawCircle(
                color = Color.White,
                radius = radius * 0.6f,
                center = center
            )
        }

        // Show tooltip for hovered segment
        hoveredSegment?.let { segment ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = mousePosition.x.toInt() + 12,
                            y = mousePosition.y.toInt() - 40
                        )
                    }
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        // Color indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(segment.color, shape = CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Category name
                        Text(
                            text = segment.category,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Amount and percentage
                    Text(
                        text = String.format("%.1f%%", segment.percentage),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


// Data class for chart items
data class ChartItem(
    val category: String,
    val amount: Double,
    val percentage: Double,
    val color: Color
)

fun getCategoryColor(category: String): Color {
    return when {
        category.contains("Food", ignoreCase = true) -> Color(0xFFFFCA28) // Yellow
        category.contains("Rent", ignoreCase = true) -> Color(0xFF42A5F5) // Blue
        category.contains("Salary", ignoreCase = true) -> Color(0xFF66BB6A) // Green
        category.contains("Interest", ignoreCase = true) -> Color(0xFF26A69A) // Teal
        category.contains("Transport", ignoreCase = true) -> Color(0xFFFFB300) // Amber
        category.contains("Shopping", ignoreCase = true) -> Color(0xFFEF5350) // Red-ish
        category.contains("Medicine", ignoreCase = true) -> Color(0xFFAB47BC) // Purple
        category.contains("Entertainment", ignoreCase = true) -> Color(0xFFFF7043) // Deep Orange
        category.contains("Gift", ignoreCase = true) -> Color(0xFF9575CD) // Light Purple
        category.contains("Networking", ignoreCase = true) -> Color(0xFF26C6DA) // Cyan
        else -> Color(0xFF78909C) // Gray
    }
}
