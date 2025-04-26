package com.example.androidfinanceapp

import android.hardware.Sensor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.LoginScreen.route,
            modifier = Modifier.padding(paddingValues = paddingValues)
        ) {
            composable(Screens.LoginScreen.route) {
                LoginScreen(navController)
            }
            composable(Screens.SignupScreen.route) {
                SignupScreen(navController)
            }
        }
    }
}