package com.example.androidfinanceapp.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.common.ManageScreenTopAppBar

@Composable
fun AssetManagementScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    assetViewModel: AssetViewModel = viewModel(factory = AssetViewModel.Factory)
) {
    // Get the JWT token from data store
    val token by dataStoreManager.tokenFlow.collectAsState("initial")


    // Track the selected tab (Add asset or manage asset)
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        assetViewModel.getAsset(token, "USD")
    }

    Scaffold(
        topBar = {
            ManageScreenTopAppBar(
                navController = navController,
                firstTabText = stringResource(R.string.add_asset_tab_text),
                secondTabText = stringResource(R.string.manage_asset_tab_text),
                selectedTab = selectedTab,
                onTabSelected = { newTab ->
                    selectedTab = newTab
                }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(top = 16.dp, bottom = 0.dp)
        ) {
            if(selectedTab == 0) {
                AddAssetTab(
                    token = token,
                    assetViewModel= assetViewModel,
                    modifier = modifier,
                )
            } else {
                ManageAssetTab(
                    token = token,
                    assetViewModel = assetViewModel,
                )
            }
        }
    }
}
