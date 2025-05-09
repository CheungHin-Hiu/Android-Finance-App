package com.example.androidfinanceapp.ui.signup

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
import com.example.androidfinanceapp.ui.Screens
import com.example.androidfinanceapp.ui.login.ErrorDialog
import com.example.androidfinanceapp.ui.login.PasswordTextField
import com.example.androidfinanceapp.ui.login.TopFinanceTitle
import com.example.androidfinanceapp.ui.login.UsernameTextField

// Sign up page for top finance
@Composable
fun SignupScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    signupViewModel: SignupViewModel = viewModel(factory = SignupViewModel.Factory)
) {
    val signupUiState = signupViewModel.signupUiState

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
        Spacer(modifier = Modifier.size(16.dp))
        PasswordTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(R.string.reenter_password)
        )
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = {
                navController.navigate(Screens.LoginScreen.route)
            }) {
                Text(stringResource(R.string.return_login_button))
            }
            Button(onClick = {signupViewModel.signup(username, password, confirmPassword)}) {
                Text(stringResource(R.string.sign_up))
            }
        }

        when(signupUiState) {
            is SignupUiState.Idle -> {
                // Do nothing and display the screen
            }
            is SignupUiState.Success -> {
                // Display showing Signup success, when button pressed, navigate to login screen
                SuccessDialog(
                    onDismissRequest = {
                        signupViewModel.setUiStateIdle()
                        navController.popBackStack(Screens.LoginScreen.route, false)
                    },
                    dialogText = stringResource(R.string.sign_up_success_dialog)
                )
            }
            is SignupUiState.Error -> {
                val errorMessage = signupUiState.message
                openAlertDialog = true
                if (openAlertDialog) {
                    ErrorDialog(
                        onDismissRequest = {
                            openAlertDialog = false
                            signupViewModel.setUiStateIdle()
                        },
                        dialogText = errorMessage,
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessDialog(
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
                        Text(text = stringResource(R.string.return_login_button))
                    }
                }
            }
        }
    )
}
