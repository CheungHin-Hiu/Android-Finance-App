package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.common.AppNavigationDrawer
import com.example.androidfinanceapp.ui.common.ScreenTopBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.Month
import java.time.Year

@Composable
fun AssetStatisticsScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var yearSelected by remember { mutableStateOf(Year.now()) }
    var currencySelected by remember { mutableStateOf("USD") }

    AppNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        currentScreen = Screens.AssetStatisticScreen
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0,0,0,0),
            topBar = {
                ScreenTopBar(
                    drawerState = drawerState, scope = scope, currentScreen = Screens.AssetStatisticScreen
                )
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
                                onTap = {
                                    navController.navigate(Screens.AssetManagementScreen.route)
                                }
                            )
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add_icon),
                        contentDescription = "Manage asset",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .scrollable(scrollState, orientation = Orientation.Vertical),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    YearDropdownList(
                        year = yearSelected,
                        onYearSelected = {newYear ->
                            yearSelected = newYear
                        }
                    )
                    CurrencyDropdownList(
                        currency = currencySelected,
                        onCurrencySelected = {newCurrency ->
                            currencySelected = newCurrency
                        }
                    )
                }


                Text(
                    text = stringResource(R.string.total_assets_title),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(95,63,255),
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )

                AssetLineChart()

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                )

                Text(
                    text = stringResource(R.string.asset_statistics_title),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(95,63,255),
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )

                AssetStatisticDonutChart()
            }
        }
    }
}

@Composable
fun YearDropdownList(
    year: Year = Year.now(),
    onYearSelected: (Year) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val years = remember {
        (2000..Year.now().value)
            .map { Year.of(it) }
            .sortedByDescending { it.value }
    }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .background(Color(225, 225, 225)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.dropdown_arrow_content_description),
                modifier = Modifier
                    .background(Color(150, 52, 182)),
                tint = Color.White
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = year.toString(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .width(120.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.height(180.dp)
        ) {
            years.forEach { yearItem ->
                DropdownMenuItem(
                    text = { Text(text = yearItem.toString()) },
                    onClick = {
                        onYearSelected(yearItem)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun CurrencyDropdownList(
    currency: String,
    onCurrencySelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currencyList = remember { listOf("HKD", "USD", "JPY", "CNY") }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .background(Color(225, 225, 225)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = currency,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .width(120.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.dropdown_arrow_content_description),
                modifier = Modifier
                    .background(Color(150, 52, 182)),
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.height(180.dp)
        ) {
            currencyList.forEach { currencyItem ->
                DropdownMenuItem(
                    text = { Text(text = currencyItem)},
                    onClick = {
                        onCurrencySelected(currencyItem)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun AssetLineChart(
    assetTotalValues: MutableList<Float> = mutableListOf<Float>(10000.00f, 20000.00f, 30000.00f, 20000.00f, 10000.00f, 20000.00f, 30000.00f, 20000.00f, 10000.00f, 20000.00f, 30000.00f, 20000.00f),
    monthsList: List<Month> = listOf(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER)
) {


    val steps = 10
    assetTotalValues.add(0, 0f)
    val assetValueMax = assetTotalValues.max()
    val standardizedAssetValue = standardizeNumber(assetTotalValues)
    val monthStringList: MutableList<String> = monthsList.map { month ->
        month.toString().substring(0,3)
    }.toMutableList()
    monthStringList.add(0, "")

    val pointData = standardizedAssetValue.mapIndexed { index, value ->
        Point(index.toFloat(), value)
    }


    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointData.size - 1)
        .labelData { i -> monthStringList[i]}
        .labelAndAxisLinePadding(4.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = assetValueMax / steps
            (i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.SmoothCurve()
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    )
                ),
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outline)
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )


}

private fun standardizeNumber(values: MutableList<Float>): MutableList<Float> {
    if (values.isEmpty()) return mutableListOf()

    val min = values.minOrNull() ?: return mutableListOf()
    val max = values.maxOrNull() ?: return mutableListOf()

    return values.map { value ->
        10 + (value - min) * (100 - 10) / (max - min)
    }.toMutableList()
}

@Preview
@Composable
fun AssetStatisticDonutChart(
    assetValues: List<Float> = listOf(300f, 200f, 300f, 400f, 500f, 600f),
    assetTypes: List<String> = listOf("Cash", "Cryptocurrency", "Stock", "Jewellery", "Real Estate", "Other")
) {

    // State to control the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }
    // State to hold the clicked slice data
    var clickedSlice by remember { mutableStateOf<PieChartData.Slice?>(null) }

    val pieChartColor = listOf(
        Color(0xFFFF0000), // Red
        Color(0xFFFFA500), // Orange
        Color(0xFFFFFF00), // Yellow
        Color(0xFF008000), // Green
        Color(0xFF0000FF), // Blue
        Color(0xFF4B0082)  // Indigo
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Legend/Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Split assets into rows of maximum 4 items
            assetTypes.chunked(4).forEach { rowAssets ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowAssets.forEachIndexed { index, assetType ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        color = pieChartColor[assetTypes.indexOf(assetType)],
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = assetType,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        val pieChartData = PieChartData(
            slices = assetValues.mapIndexed { index, percentage ->
                PieChartData.Slice(assetTypes[index], percentage, color = pieChartColor[index])
            },
            plotType = PlotType.Pie
        )

        val pieChartConfig = PieChartConfig(
            showSliceLabels = false,
            isAnimationEnable = true,
            animationDuration = 1500,
            backgroundColor = Color.Transparent
        )

        PieChart(
            modifier = Modifier
                .size(300.dp)
                .aspectRatio(1f),
            pieChartData,
            pieChartConfig,
            onSliceClick = { slice ->
                clickedSlice = slice
                showDialog = true
            }
        )

        // Display the dialog if showDialog is true
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Asset Details") },
                text = {
                    Text(text = "Name: ${clickedSlice?.label}\nValue: ${clickedSlice?.value}")
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }

}

