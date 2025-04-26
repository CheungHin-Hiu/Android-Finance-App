package com.example.androidfinanceapp.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidfinanceapp.R
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
                username = ""
                password = ""
                confirmPassword = ""
            }) {
                Text(stringResource(R.string.clear_button_text))
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
