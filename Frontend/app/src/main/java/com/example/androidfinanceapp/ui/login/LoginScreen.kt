package com.example.androidfinanceapp.ui.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.data.DataStoreManager
import com.example.androidfinanceapp.ui.Screens
import kotlinx.coroutines.launch


// Login screen for Top Finance
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
) {
    val loginUiState = loginViewModel.loginUiState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var openAlertDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        TopFinanceTitle(modifier = modifier)
        Spacer(modifier = Modifier.size(16.dp))
        Image(
            painter = painterResource(R.drawable.top_finance_logo),
            contentDescription = stringResource(R.string.top_finance_logo)
        )
        Spacer(modifier = Modifier.size(16.dp))
        UsernameTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(R.string.enter_user_name),
        )
        Spacer(modifier = Modifier.size(16.dp))
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.enter_password)
        )
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = {
                navController.navigate(Screens.SignupScreen.route)
            }) {
                Text(stringResource(R.string.sign_up))
            }
            Button(onClick = {loginViewModel.login(username, password)}) {
                Text(stringResource(R.string.login_button_text))
            }

        }
    }

    when(loginUiState) {
        is LoginUiState.Idle -> {
            // Do nothing and display the screen
        }
        is LoginUiState.Success -> {
            val loginResponse = loginUiState.loginResponse
            // Saved the received the received user external id and JWT token in DataStore
            coroutineScope.launch {
                dataStoreManager.saveLoginData(loginResponse.userId, loginResponse.userName, loginResponse.token)
            }
            navController.navigate(Screens.OverviewScreen.route)
        }
        is LoginUiState.Error -> {
            val errorMessage = loginUiState.message
            openAlertDialog = true
            if (openAlertDialog) {
                ErrorDialog(
                    onDismissRequest = {
                        openAlertDialog = false
                        loginViewModel.setUiStateIdle()
                   },
                    dialogText = errorMessage,
                )
            }
        }
    }
}

// Composable function for Top Finance's title
@Composable
fun TopFinanceTitle(modifier: Modifier){
    Text(
        text = stringResource(R.string.top_finance),
        textAlign = TextAlign.Center,
        color = Color(109, 209, 145),
        style = TextStyle(
            fontSize = 48.sp,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = modifier
    )
}

// Composable function for text field for entering user name
@Composable
fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(0.8f),
        singleLine = true
    )
}

// Composable function for text field for enter user password / confirm password
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(0.8f),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    dialogText: String,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dialogText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(R.string.dismiss_button))
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun ErrorDialogPreview(onDismissRequest: () -> Unit = {}, dialogText: String = "haha") {
    ErrorDialog(
        onDismissRequest = onDismissRequest,
        dialogText = dialogText,
    )
}

