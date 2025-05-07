package com.example.androidfinanceapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.ui.Screens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScreenTopBar(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentScreen: Screens
){
    val screenTitle = when(currentScreen) {
        is Screens.OverviewScreen -> "Overview";
        is Screens.TargetScreen -> "Target";
        is Screens.AssetStatisticScreen -> "Asset";
        else -> "Title"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
    ) {
        // Menu Icon aligned to the left
        IconButton(
            onClick = { scope.launch { drawerState.open() } },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.Black
            )
        }

        // Screen Title
        Text(
            text = screenTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // Bottom divider line
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 1.dp,
            color = Color(0xFFE6E0F0)
        )
    }
}

@Composable
fun ManageScreenTopAppBar(
    navController: NavController,
    firstTabText: String,
    secondTabText: String,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFF8F0FF)) // Light purple background color
    ) {
        // Back button
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_text),
                tint = Color.Black
            )
        }

        // Expense/Income toggle in center
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp)
                .width(220.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Expense Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (selectedTab == 0) Color(0xFF7C4DFF) else Color.White
                        )
                        .clickable { onTabSelected(0) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstTabText,
                        color = if (selectedTab == 0) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Income Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (selectedTab == 1) Color(0xFF7C4DFF) else Color.White
                        )
                        .clickable { onTabSelected(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = secondTabText,
                        color = if (selectedTab == 1) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // More options menu
        IconButton(
            onClick = { /* Handle menu click */ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_options_button_description),
                tint = Color.Black
            )
        }
    }
}