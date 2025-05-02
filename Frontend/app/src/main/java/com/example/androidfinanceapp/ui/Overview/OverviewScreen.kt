package com.example.androidfinanceapp.ui.Overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.data.DataStoreManager

@Composable
fun OverviewScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    dataStoreManager: DataStoreManager,
    overviewViewModel: OverviewViewModel = viewModel(factory = OverviewViewModel.Factory)
) {
    val getTransactionState = overviewViewModel.getTransactionState
    val addTransactionState = overviewViewModel.addTransactionState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var openAlertDialog by remember { mutableStateOf(false) }
    

}