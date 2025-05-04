package com.example.androidfinanceapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    return Unit
}