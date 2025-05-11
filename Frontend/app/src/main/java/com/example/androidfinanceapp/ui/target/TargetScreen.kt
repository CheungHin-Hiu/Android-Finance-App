package com.example.androidfinanceapp.ui.target

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.common.AppNavigationDrawer
import com.example.androidfinanceapp.ui.common.ScreenTopBar
import com.example.androidfinanceapp.ui.theme.ErrorButton
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

sealed class TargetType(val name: String, val primary_color: Long, val secondary_color: Long) {
    object Saving : TargetType("Saving", 0xFF5C0A0A, 0xFFFCCACA)
    object Budget : TargetType("Budget", 0xFF4B27B8, 0xFFC7B6FC)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    targetViewModel: TargetViewModel = viewModel(
        factory = TargetViewModel.Factory
    )
) {
    // set idle
    targetViewModel.setGetIdle()

    val token by dataStoreManager.tokenFlow.collectAsState(initial = "initial")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var savingAmount = 0f
    var savingTarget = 0f
    var budgetAmount = 0f
    var budgetTarget = 0f

    // handle target state changes
    LaunchedEffect(Unit) {
        targetViewModel.getTarget(token, "USD")
    }

    targetViewModel.targets.forEach { target ->
        if (target.type == "Budget") {
            // to-do: create amount endpoint
            budgetAmount = target.convertedAmount.toFloat()
            budgetTarget = target.convertedAmount.toFloat()
        } else {
            savingAmount = target.convertedAmount.toFloat()
            savingTarget = target.convertedAmount.toFloat()
        }
    }

    AppNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        currentScreen = Screens.TargetScreen
    ) {
        Scaffold(
            // Remove default content padding from the scaffold
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                ScreenTopBar(
                    drawerState = drawerState, scope = scope, currentScreen = Screens.TargetScreen
                )
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (savingAmount > 0f) {
                    // Saving Target (to be replaced with db data)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TargetProgressCircle(
                            targetType = TargetType.Saving,
                            currentAmount = savingAmount,
                            target = savingTarget
                        )
                    }
                } else {
                    // Saving Target (to be replaced with db data)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No saving target found.")
                    }
                }

                if (budgetAmount > 0f) {
                    // Budget Target (to be replaced with db data)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TargetProgressCircle(
                            targetType = TargetType.Budget,
                            currentAmount = budgetAmount,
                            target = budgetTarget
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No budget target found.")
                    }
                }

                // set target
                Button(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Create New Target")
                }

                // remove target
                Button(
                    onClick = {
                        targetViewModel.deleteTarget(token)
                        targetViewModel.getTarget(token, "USD")
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Remove Targets")
                }
            }

            // bottom sheet for setting new target
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // Sheet content
                    NewTargetForm(
                        onDismiss = { showBottomSheet = false },
                        targetViewModel,
                        onSave = {
                            targetType, currency, newValue ->
                            targetViewModel.addTarget(token, targetType, currency, newValue.toDouble())
                            targetViewModel.getTarget(token, "USD")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TargetProgressCircle(targetType: TargetType, currentAmount: Float, target: Float) {
    var percentage = if (target > 0) currentAmount / target else 0f
    percentage = min(percentage, 1.toFloat())

    val sweepAngle = 360 * percentage
    val splitAngle = 2f

    Box(
        modifier = Modifier
            .size(250.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(250.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = min(canvasWidth, canvasHeight) / 2

            // Draw the progress arc
            drawArc(
                color = Color(targetType.secondary_color),
                startAngle = -90f + sweepAngle + splitAngle,
                sweepAngle = 360f - sweepAngle - 2 * splitAngle,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx()),
                topLeft = Offset(
                    x = (canvasWidth - radius * 2) / 2,
                    y = (canvasHeight - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2)
            )

            // Draw the filled arc
            drawArc(
                color = Color(targetType.primary_color),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx()),
                topLeft = Offset(
                    x = (canvasWidth - radius * 2) / 2,
                    y = (canvasHeight - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2)
            )
        }

        // Add text at the center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${targetType.name} Target", // Display percentage
                color = Color.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "$${round(currentAmount*100)/100}/$${round(target*100)/100}", // Display percentage
                color = Color.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTargetForm(onDismiss: () -> Unit, targetViewModel: TargetViewModel, onSave: (TargetType, String, String) -> Unit) {
    var selectedTargetType by remember { mutableStateOf<TargetType>(TargetType.Saving) }
    var newTargetValue by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("HKD") }
    val currencies = listOf("HKD", "USD", "JPY", "CNY")
    val currencySymbols = mapOf(
        "HKD" to "$",
        "USD" to "$",
        "JPY" to "¥",
        "CNY" to "¥"
    )

    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "New Target", fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            thickness = 1.dp,
        )

        Spacer(modifier = Modifier.height(15.dp))

        // target type
        var selectedIndex by remember { mutableIntStateOf(0) }
        val targetTypes = listOf(TargetType.Saving, TargetType.Budget)

        SingleChoiceSegmentedButtonRow {
            targetTypes.forEachIndexed { index, target ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = targetTypes.size
                    ),
                    onClick = {
                        selectedIndex = index
                        selectedTargetType = target
                    },
                    selected = index == selectedIndex,
                    label = { Text(target.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // target amount and currency
        Row(verticalAlignment = Alignment.CenterVertically) {

            // currency symbol
            val currencySymbol = currencySymbols[selectedCurrency] ?: "$"
            Text(
                text = currencySymbol,
                fontSize = 22.sp, // Increased font size
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(5.dp))

            // target amount input
            TextField(
                value = newTargetValue,
                onValueChange = {
                    newTargetValue = when {
                        it.isEmpty() -> "" // Keep it empty if input is empty
                        it.all { char -> char.isDigit() } -> it // Accept only digits
                        else -> newTargetValue // Keep the current value if input is invalid
                    }
                },
                label = { Text("Target Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(200.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            // currency options
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

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Currency",
                    modifier = Modifier.size(28.dp), // Increased icon size
                    tint = Color.Black
                )
            }

            // expanded list of currencies for selection
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

        Spacer(modifier = Modifier.height(15.dp))

        // save & discard button
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                modifier = Modifier.width(100.dp),
                onClick = {
                    onSave(selectedTargetType, selectedCurrency, newTargetValue)
                    onDismiss()
                }
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = ErrorButton),
                modifier = Modifier.width(100.dp),
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Discard")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TargetPreview() {
//    NewTargetForm({})
//}