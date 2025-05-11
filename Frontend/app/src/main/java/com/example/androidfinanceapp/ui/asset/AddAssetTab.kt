package com.example.androidfinanceapp.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.ui.common.CategoryGrid
import com.example.androidfinanceapp.ui.common.CategoryItem
import com.example.androidfinanceapp.ui.common.KeypadGrid
import com.example.androidfinanceapp.ui.login.ErrorDialog
import com.example.androidfinanceapp.ui.signup.SuccessDialog

val currencies = listOf("HKD", "USD", "JPY", "CNY")

val currencySymbols = mapOf(
    "HKD" to "$",
    "USD" to "$",
    "JPY" to "¥",
    "CNY" to "¥"
)

val stockCode = listOf("AAPL", "AMZN", "GOOG", "NVDA")

val cryptoCode = listOf("BTC", "ETH", "USDT", "DOGE")

val assetCategories =
    listOf(
        CategoryItem(1, R.drawable.salary, "Cash"),
        CategoryItem(2, R.drawable.cryptocurrency, "Crypto"),
        CategoryItem(3, R.drawable.stock_market, "Stock"),
        CategoryItem(4, R.drawable.jewellery, "Jewellery"),
        CategoryItem(5, R.drawable.real_estate, "Real Estate"),
        CategoryItem(6, R.drawable.other_asset, "Other")
    )
@Composable
fun AddAssetTab(
    token: String,
    assetViewModel: AssetViewModel,
    modifier: Modifier,
) {
    // State for the whole screen
    val addAssetState = assetViewModel.assetState

    // State for tracking the selected asset category
    var selectedCategory by remember { mutableStateOf(CategoryItem(1, R.drawable.salary, "Cash")) }

    var amountValue by remember { mutableStateOf("") }

    // State for tracking the selected asset category

    // State for tracking the selected currency and amount
    var selectedCurrency by remember { mutableStateOf("HKD") }
    var currencyAmount by remember { mutableStateOf("0.0") }

    // State for tracking the selected stock and number of share
    var selectedStock by remember { mutableStateOf("AAPL") }
    var stockShare by remember { mutableStateOf("0.0") }

    // State for tracking the selected cryptocurrency and amount
    var selectedCrypto by remember { mutableStateOf("BTC") }
    var cryptoAmount by remember { mutableStateOf("0.0") }

    val currencySymbol = currencySymbols[selectedCurrency] ?: "$"

    // Track whether the dropdown list should be expanded or not
    var expanded by remember { mutableStateOf(false) }

    var openAlertDialog by remember { mutableStateOf(false) }


    CategoryGrid(
        categories = assetCategories,
        selectedCategory = selectedCategory,
        onCategorySelected = { category ->
            selectedCategory = category
            currencyAmount = "0.0"
            stockShare = "0.0"
            cryptoAmount = "0.0"
        },
        modifier = modifier.padding(start = 10.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 3.dp),
            thickness = 2.dp,
            color = Color.Black
        )

        when(selectedCategory) {
            assetCategories[2] -> AmountAndAssetSection(
                assetSymbol = "Share:",
                assetAmount = stockShare,
                assetCode = selectedStock,
                assetList = stockCode,
                expanded = expanded,
                onAssetCodeClick = {
                    expanded = true
                },
                onDismissDropDownClick = {
                    expanded = false
                },
                onDropDownItemClick = { newStock ->
                    selectedStock = newStock
                    expanded = false
                }
            )
            assetCategories[1] -> AmountAndAssetSection(
                assetSymbol = "Coin:",
                assetAmount = cryptoAmount,
                assetCode = selectedCrypto,
                assetList = cryptoCode,
                expanded = expanded,
                onAssetCodeClick = {
                    expanded = true
                },
                onDismissDropDownClick = {
                    expanded = false
                },
                onDropDownItemClick = { newCrypto ->
                    selectedCrypto = newCrypto
                    expanded = false
                }
            )
            else -> AmountAndAssetSection(
                assetSymbol = currencySymbol,
                assetAmount = currencyAmount,
                assetCode = selectedCurrency,
                assetList = currencies,
                expanded = expanded,
                onAssetCodeClick = {
                    expanded = true
                },
                onDismissDropDownClick = {
                    expanded = false
                },
                onDropDownItemClick = { newCurrencies ->
                    selectedCurrency = newCurrencies
                    expanded = false
                }
            )
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
                .background(Color(0xFFF0E6FF))
                .padding(top = 10.dp)
        ) {
            KeypadGrid(
                onAmountChanged = { newAmount ->
                    when (selectedCategory) {
                        assetCategories[1] -> cryptoAmount = newAmount
                        assetCategories[2] -> stockShare = newAmount
                        else -> currencyAmount = newAmount
                    }
                    amountValue = newAmount
                },
                onOkPressed = {
                    when (selectedCategory) {
                        assetCategories[1] -> {
                            assetViewModel.addAsset(
                                token = token,
                                category = selectedCategory.name,
                                type = selectedCrypto,
                                value = cryptoAmount.toFloat()
                            )
                        }

                        assetCategories[2] -> {
                            assetViewModel.addAsset(
                                token = token,
                                category = selectedCategory.name,
                                type = selectedStock,
                                value = stockShare.toFloat()
                            )
                        }

                        else -> {
                            assetViewModel.addAsset(
                                token = token,
                                category = selectedCategory.name,
                                type = selectedCurrency,
                                value = currencyAmount.toFloat()
                            )
                        }
                    }
                },
                key = selectedCategory
            )
        }
    }

    when(addAssetState) {
        is AssetState.Idle -> {
            // Do nothing
        }
        is AssetState.SuccessAdding -> {
            SuccessDialog(
                onDismissRequest = {
                    assetViewModel.setStateIdle()
                    assetViewModel.getAsset(token, "USD")
                },
                dialogText = stringResource(R.string.success_adding_new_asset),
                buttonText = stringResource(R.string.confirm)
            )
        }
        is AssetState.Error -> {
            val errorMessage = addAssetState.message
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

@Composable
fun AmountAndAssetSection(
    assetSymbol: String,
    assetAmount: String,
    assetCode: String,
    assetList: List<String>,
    expanded: Boolean,
    onAssetCodeClick: () -> Unit,
    onDismissDropDownClick: () -> Unit,
    onDropDownItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp), // Reduced vertical padding to move closer to divider
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = assetSymbol,
                fontSize = 22.sp, // Increased font size
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(2.dp))

            Text(
                text = assetAmount.ifEmpty { "0" },
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

        Row(
            modifier = Modifier
                .clickable { onAssetCodeClick() }
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = assetCode,
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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissDropDownClick
            ) {
                assetList.forEach { asset ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = asset,
                                fontSize = 18.sp
                            )
                        },
                        onClick = {onDropDownItemClick(asset)}
                    )
                }
            }
        }

    }
}