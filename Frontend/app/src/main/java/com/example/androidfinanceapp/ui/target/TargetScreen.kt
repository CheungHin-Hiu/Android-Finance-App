package com.example.androidfinanceapp.ui.target

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.common.AppNavigationDrawer
import com.example.androidfinanceapp.ui.common.ScreenTopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TargetScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    LaunchedEffect() {
//
//    }

    AppNavigationDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        currentScreen = Screens.TargetScreen
    ) {
        Scaffold(
            // Remove default content padding from the scaffold
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                ScreenTopBar(
                    drawerState = drawerState, scope = scope, currentScreen = Screens.TargetScreen
                )
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Saving Target
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(315.dp)
                ) {

                }

                // Budget Target
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(315.dp)
                ) {

                }

                // set/remove target
            }
        }
    }
}