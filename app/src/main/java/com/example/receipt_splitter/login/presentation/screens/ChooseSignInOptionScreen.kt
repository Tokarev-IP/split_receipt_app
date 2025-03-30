package com.example.receipt_splitter.login.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.receipt_splitter.R
import com.example.receipt_splitter.login.presentation.LoginUiErrorIntent
import com.example.receipt_splitter.login.presentation.LoginUiEvent
import com.example.receipt_splitter.login.presentation.LoginUiState
import com.example.receipt_splitter.login.presentation.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSignInOptionScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
) {
    val uiState by loginViewModel.getUiStateFlow().collectAsState()
    val uiErrorIntent by loginViewModel.getUiErrorIntentFlow().collectAsState(null)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.sign_in))
                }
            )
        },
    ) { innerPadding ->
        when (uiState) {

            is LoginUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                )
            }

            is LoginUiState.Show -> {
                ChooseSignInOptionView(
                    modifier = Modifier.padding(innerPadding),
                    onGoogleSignInClick = {
                        loginViewModel.setUiEvent(LoginUiEvent.LoginWithGoogleWasClicked)
                    },
                    isEnable = { uiState is LoginUiState.Show }
                )
            }
        }

        when (uiErrorIntent) {
            is LoginUiErrorIntent.UiError -> {
                Toast.makeText(
                    LocalContext.current,
                    (uiErrorIntent as LoginUiErrorIntent.UiError).msg,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun ChooseSignInOptionView(
    modifier: Modifier = Modifier,
    onGoogleSignInClick: () -> Unit,
    isEnable: () -> Boolean,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedButton(
            onClick = { onGoogleSignInClick() },
            enabled = isEnable(),
        ) {
            Text(text = "Sign In with Google")
        }
    }
}