package com.example.androidfinanceapp.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidfinanceapp.ui.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavigationDrawer(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentScreen: Screens,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color(0xFFF8F0FF)
            ) {
                // App Name/Logo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Finance App",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                }

                // Divider
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFE6E0F0)
                )

                // Navigation Items (Overview)
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    selected = currentScreen is Screens.OverviewScreen,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screens.OverviewScreen.route)
                        }
                    },
                    label = {
                        Text(
                            text = "Overview",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )

                // Navigation Items (Target)
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    selected = currentScreen is Screens.TargetScreen,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screens.TargetScreen.route)
                        }
                    },
                    label = {
                        Text(
                            text = "Target",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )

                // Navigation Items (Statistic)
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    selected = currentScreen is Screens.AssetStatisticScreen,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screens.AssetStatisticScreen.route)
                        }
                    },

                    label = {
                        Text(
                            text = "Assets Statistics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )

                // NavigationItem (Log out)
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            // Log out
                            scope.launch {
                                navController.navigate(Screens.LoginScreen.route)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                )
            }
        }
    ) {
        content()
    }
}