package com.example.androidfinanceapp.ui.target

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.common.AppNavigationDrawer
import com.example.androidfinanceapp.ui.common.ScreenTopBar
import kotlin.math.min
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var savingAmount by remember { mutableFloatStateOf(0f) }
    var savingTarget by remember { mutableFloatStateOf(0f) }
    var budgetAmount by remember { mutableFloatStateOf(0f) }
    var budgetTarget by remember { mutableFloatStateOf(0f) }

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
                // Saving Target (to be replaced with db data)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TargetProgressCircle(
                        targetType = TargetType.Saving,
                        currentAmount = 8000.toFloat(),
                        target = 10000.toFloat()
                    )
                }

                // Budget Target (to be replaced with db data)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TargetProgressCircle(
                        targetType = TargetType.Budget,
                        currentAmount = 500.toFloat(),
                        target = 10000.toFloat()
                    )
                }

                // set target
                Button(onClick = { showBottomSheet = true }) {
                    Text("Set a New Target")
                }

                // remove target
                Button(onClick = { }) {
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
                    NewTargetForm()
                }
            }
        }
    }
}

sealed class TargetType(val name: String, val primary_color: Long, val secondary_color: Long) {
    object Saving : TargetType("Saving", 0xFF5C0A0A, 0xFFFCCACA)
    object Budget : TargetType("Budget", 0xFF4B27B8, 0xFFC7B6FC)
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
                text = "$${currentAmount.roundToInt()}/$${target.roundToInt()}", // Display percentage
                color = Color.Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTargetForm() {
    var selectedTargetType by remember { mutableStateOf<TargetType?>(null) }
//    var newTargetValue by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "New Target", fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            thickness = 1.dp,
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Target Type: ", modifier = Modifier.padding(end = 8.dp))
            var expandedTargetType by remember { mutableStateOf(false) }
            val targetTypes = listOf(TargetType.Saving, TargetType.Budget)

            ExposedDropdownMenuBox(
                expanded = expandedTargetType,
                onExpandedChange = { expandedTargetType = it }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedTargetType?.let { it::class.simpleName ?: "" } ?: "Select Type",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTargetType)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expandedTargetType,
                    onDismissRequest = { expandedTargetType = false }
                ) {
                    targetTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                selectedTargetType = type
                                expandedTargetType = false
                            })
                    }
                }
            }
        }

//        Spacer(modifier = Modifier.height(10.dp))
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("Target Value: ", modifier = Modifier.padding(end = 8.dp))
//            TextField(
//                value = newTargetValue.toString(),
//                onValueChange = {
//                    val filteredValue = it.replace(Regex("[^0-9]"), "")
//                    newTargetValue = filteredValue.toInt()
//                },
//                modifier = Modifier.weight(1f),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    keyboardType = KeyboardType.Number
//                ),
//                placeholder = { Text("Enter value") }
//            )
//        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun TargetPreview() {
//    NewTargetForm()
//}