package com.example.androidfinanceapp.ui.asset

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.common.ManageScreenTopAppBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun AssetManagementScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    manageAssetViewModel: ManageAssetViewModel = viewModel(factory = ManageAssetViewModel.Factory)
) {
    // Get the JWT token from data store
    val token by dataStoreManager.tokenFlow.collectAsState(initial = null)

    // Track the selected tab (Add asset or manage asset)
    var selectedTab by remember { mutableStateOf(0) }

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
                    navController = navController,
                    modifier = modifier,
                )
            } else {
                ManageAssetTab(
                    token = token,
                    navController = navController,
                    modifier = modifier
                )
            }
        }
    }
}
