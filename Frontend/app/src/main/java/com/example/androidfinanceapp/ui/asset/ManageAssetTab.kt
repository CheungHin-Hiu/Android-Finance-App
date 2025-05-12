package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.ui.login.ErrorDialog
import com.example.androidfinanceapp.ui.signup.SuccessDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val assetTypeList = listOf(
    "All",
    "Cash",
    "Cryptocurrency",
    "Stock",
    "Jewellery",
    "Real Estate",
    "Other"
)

val dateRangeTypeList = listOf(
    "This week",
    "This month",
    "Last 6 month",
    "Last 12 month",
    "All",
)

@Composable
fun ManageAssetTab(
    token: String,
    assetViewModel: AssetViewModel,
) {
    var assetCategory by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("This week") }

    val modifyAssetState = assetViewModel.assetState
    val filteredAssetList = filterAssetList(assetViewModel.assetList, assetCategory, dateRange)

    var openAlertDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AssetDropDownList(
            label = stringResource(R.string.asset_type_dropdown_label),
            selectedOption = assetCategory,
            options = assetTypeList,
            onListItemClick = { newAssetCategory ->
                assetCategory = newAssetCategory
            }
        )

        AssetDropDownList(
            label = stringResource(R.string.time_dropdown_label),
            selectedOption = dateRange,
            options = dateRangeTypeList,
            onListItemClick = { newDateRange ->
                dateRange = newDateRange
            }
        )
    }

    HorizontalDivider(
        thickness = 3.dp,
        modifier = Modifier
            .padding(top = 5.dp, bottom = 5.dp)
    )

    LazyColumn {
        items(filteredAssetList) { asset ->
            val amountSuffix = getAmountSuffix(asset.category.toString())
            AssetManagementCard(
                description = asset.description,
                amount =  asset.amount.toString(),
                value = asset.value.toString(),
                amountSuffix = amountSuffix,
                createdAt = "Created at: " + transformDate(asset.createdAt),
                updatedAt = "Updated at: "+ transformDate(asset.updatedAt),
                onModifyClick = { newAmount ->
                    assetViewModel.modifyAsset(
                        token =  token,
                        id = asset.id,
                        amount = newAmount.toFloat(),
                        category = asset.category,
                        type = asset.type,
                    )
                },
                onDeleteClick = {
                    assetViewModel.deleteAsset(
                        token = token,
                        id = asset.id
                    )
                }
            )
        }
    }

    when(modifyAssetState) {
        is AssetState.Idle -> {
            // Do nothing
        }
        is AssetState.SuccessModifying -> {
            SuccessDialog(
                onDismissRequest = {
                    assetViewModel.setStateIdle()
                    assetViewModel.getAsset(token, currency = "USD")
                },
                dialogText = stringResource(R.string.success_modifying_asset_value),
                buttonText = stringResource(R.string.confirm)
            )
        }
        is AssetState.SuccessDeleting -> {
            SuccessDialog(
                onDismissRequest = {
                    assetViewModel.setStateIdle()
                    assetViewModel.getAsset(token, currency = "USD")
                },
                dialogText = stringResource(R.string.successfully_delete_asset),
                stringResource(R.string.confirm)
            )
        }
        is AssetState.Error -> {
            val errorMessage = modifyAssetState.message
            openAlertDialog = true
            if (openAlertDialog) {
                ErrorDialog(
                    onDismissRequest = {
                        openAlertDialog = false
                        assetViewModel.setStateIdle()
                    },
                    dialogText = errorMessage
                )
            }
        }
        else -> {
            // Do nothing
        }
    }
}

fun getAmountSuffix(categoryString: String): String {
    val suffix = when(categoryString) {
        "Coin" -> " Coin"
        "Stock" -> " Share"
        else -> ""
    }
    return suffix
}

fun transformDate(inputDate: String): String {
    // Define the input and output date formats
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm")

    // Parse the input string to a LocalDateTime object and reformat it
    val dateTime = LocalDateTime.parse(inputDate, inputFormatter)
    return dateTime.format(outputFormatter)
}

fun filterAssetList(
    assetList: MutableList<Asset>,
    assetType: String,
    dateRange: String,
): List<Asset> {
    // Define a date formatter that matches the format of the createdAt string.
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // Get the current date
    val currentDate = LocalDate.now()

    // Determine the start date based on the selected date range
    val startDate = when (dateRange) {
        "This week" -> currentDate.with(java.time.DayOfWeek.MONDAY)
        "This month" -> currentDate.withDayOfMonth(1)
        "Last 6 month" -> currentDate.minusMonths(6).withDayOfMonth(1)
        "Last 12 month" -> currentDate.minusYears(1).withDayOfMonth(1)
        else -> null // "All" or any other case
    }

    return assetList.filter { asset ->
        // Filter by asset type
        val matchesType = assetType == "All" || asset.category == assetType

        // Parse the createdAt date
        val assetDate = LocalDate.parse(asset.createdAt, formatter)

        // Filter by date range
        val matchesDateRange = startDate == null || !assetDate.isBefore(startDate)

        // Return true if both filters match
        matchesType && matchesDateRange
    }
}


@Composable
fun AssetDropDownList(
    label: String,
    selectedOption: String,
    options: List<String>,
    onListItemClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.padding(bottom = 4.dp))

        OutlinedCard(
            modifier = Modifier
                .size(width = 160.dp, height = 50.dp)
                .clickable(onClick = { expanded = !expanded })
                .animateContentSize(), // Smooth animation for size changes
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp), // Padding for consistent spacing
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 18.sp, // Adjusted to fit better visually
                    )
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown_arrow_content_description),
                    modifier = Modifier
                        .size(24.dp) // Adjusted size for a better fit
                        .align(Alignment.CenterVertically)
                )
            }
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(160.dp) // Matches the card width
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {Text(option)},
                    onClick = {
                        onListItemClick(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun AssetManagementCard(
    description: String,
    amount: String,
    value: String,
    amountSuffix: String,
    createdAt: String,
    updatedAt: String,
    onModifyClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = Color(234, 221, 255, 255),
            contentColor = Color.Black,
            disabledContainerColor = Color(234, 221, 255),
            disabledContentColor = Color.Black,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = description,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.padding(bottom = 2.dp))
                Text(
                    text = "Hold: $amount $amountSuffix",
                    style = TextStyle(
                        color = Color(71, 42, 124),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.padding(bottom = 2.dp))
                Text(
                    text = "Value: $value USD($)",
                    style = TextStyle(
                        color = Color(71, 42, 124),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.padding(bottom = 2.dp))
                Text(
                    text = createdAt,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )
                )
                Spacer(Modifier.padding(bottom = 1.dp))
                Text(
                    text = updatedAt,
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )
                )
            }
            Button(onClick = {
                showDialog = true
            }) {
                Text(stringResource(R.string.manage_button))
            }
        }
    }

    if (showDialog) {
        ManageAssetPopUp(
            description = description,
            currentAmount = amount,
            onDelete = {
                onDeleteClick()
                showDialog = false
            },
            onModifyConfirm = { newAmount ->
                Log.d("New amount in message pop", newAmount.toString())
                onModifyClick(newAmount)
                showDialog = false
            },
            onDismissRequest = {
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAssetPopUp(
    description: String,
    currentAmount: String,
    onDelete: () -> Unit,
    onModifyConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textFieldAmount by remember { mutableStateOf(TextFieldValue(currentAmount)) }

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween, // Adjusted for better spacing
                    horizontalAlignment = Alignment.Start
                ) {
                    // Title
                    Text(
                        text = "Manage Asset",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Description
                    Text(
                        text = description,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Input Field
                    OutlinedTextField(
                        value = textFieldAmount,
                        onValueChange = { newValue ->
                            if (newValue.text.isEmpty() || newValue.text.matches(Regex("^\\d*(\\.\\d*)?$"))) {
                                textFieldAmount = newValue
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(stringResource(R.string.modify_amount_of_asset_text_field)) },
                        placeholder = { Text(stringResource(R.string.enter_new_amount_placeholder)) }, // Added placeholder
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Text(text = "Enter a valid amount (e.g., 100.50)")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Added spacing

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Delete Button
                        Button(
                            onClick = { onDelete() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_button))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.delete_button))
                        }

                        // Modify Button
                        Button(
                            onClick = { onModifyConfirm(textFieldAmount.text) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.modify_button))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.modify_button))
                        }
                    }
                }
            }
        }
    )
}