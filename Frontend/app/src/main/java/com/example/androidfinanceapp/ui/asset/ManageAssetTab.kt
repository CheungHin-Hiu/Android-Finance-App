package com.example.androidfinanceapp.ui.asset

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.androidfinanceapp.R


val assetList = mutableListOf(
    Asset(
        id = 1,
        description = "Stock: AAPL",
        category = "Stock",
        type = "AAPL",
        amount = 100.00f,
        value = 100.00f,
        createdAt = "haha",
        updatedAt = "haha"
    )
)

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
    token: String?,
    navController: NavController,
    modifier: Modifier,
) {
    var assetType by remember { mutableStateOf("All") }
    var dateRange by remember { mutableStateOf("This week") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AssetDropDownList(
            label = stringResource(R.string.asset_type_dropdown_label),
            selectedOption = assetType,
            options = assetTypeList,
            onListItemClick = { newAssetType ->
                assetType = newAssetType
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
        items(assetList) { asset ->
            AssetManagementCard(
                asset.description,
                asset.amount.toString(),
                asset.value.toString(),
                asset.createdAt,
                asset.updatedAt,
            )
        }
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
    createdAt: String,
    updatedAt: String,
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
                    text = amount,
                    style = TextStyle(
                        color = Color(71, 42, 124),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(Modifier.padding(bottom = 2.dp))
                Text(
                    text = value,
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
                showDialog = false
            },
            onModifyConfirm = {
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
                            onClick = { onModifyConfirm(currentAmount) },
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

@Preview(showBackground = true)
@Composable
fun PreviewManageAssetTab() {
    // Mock NavController for preview purposes
    val navController = rememberNavController()

    // Mock token value
    val token = "mockToken"

    ManageAssetTab(
        token = token,
        navController = navController,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}