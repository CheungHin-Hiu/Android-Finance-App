package com.example.androidfinanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Overview.IncomeAndExpenseScreen
import com.example.androidfinanceapp.ui.Overview.IncomeAndExpenseViewModel
import com.example.androidfinanceapp.ui.Overview.OverviewScreen
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.login.LoginScreen
import com.example.androidfinanceapp.ui.signup.SignupScreen
import com.example.androidfinanceapp.ui.theme.AndroidFinanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidFinanceAppTheme {
               TopFinanceApp()
            }
        }
    }
}

@Composable
fun TopFinanceApp() {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val navController = rememberNavController()
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.LoginScreen.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screens.LoginScreen.route) {
                LoginScreen(navController, dataStoreManager)
            }
            composable(Screens.SignupScreen.route) {
                SignupScreen(navController)
            }
            composable(Screens.OverviewScreen.route){
                OverviewScreen(navController, dataStoreManager)
            }
            composable(Screens.IncomeAndExpenseScreen.route){
                IncomeAndExpenseScreen(
                    navController = navController,
                    dataStoreManager = dataStoreManager,
                )
            }
        }
    }
}