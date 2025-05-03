package com.example.androidfinanceapp.ui.Overview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.data.DataStoreManager

@Composable
fun IncomeAndExpenseScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    incomeAndExpenseViewModel: IncomeAndExpenseViewModel = viewModel(
        factory = IncomeAndExpenseViewModel.Factory
    )
) {
    // Rest of the code
}