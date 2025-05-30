package com.iliatokarev.receipt_splitter_app.login.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginNavigationEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiMessageIntent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiState
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginViewModel
import com.iliatokarev.receipt_splitter_app.login.presentation.views.ResetPasswordScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
) {
    val uiState by loginViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var resetPasswordTextFieldErrorState by remember {
        mutableStateOf(ResetPasswordTextFieldErrorState())
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow().collect { uiMessageIntent ->
            handleUiMessages(
                uiMessageIntent = uiMessageIntent,
                onResetPasswordTextFieldErrorState = { state ->
                    resetPasswordTextFieldErrorState = state
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.reset_password))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            loginViewModel.setNavigationEvent(LoginNavigationEvent.GoNavigationBack)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(R.string.go_back_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is LoginUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                )
            }
        }

        ResetPasswordScreenView(
            modifier = modifier.padding(innerPadding),
            enabled = { uiState is LoginUiState.Show },
            onResetPasswordClick = { email ->
                loginViewModel.setEvent(LoginEvent.SendResetPasswordRequest(email = email))
            },
            resetPasswordFieldErrorState = { resetPasswordTextFieldErrorState },
            onResetPasswordFieldState = { state ->
                resetPasswordTextFieldErrorState = state
            }
        )
    }
}

private fun handleUiMessages(
    uiMessageIntent: LoginUiMessageIntent,
    onResetPasswordTextFieldErrorState: (ResetPasswordTextFieldErrorState) -> Unit,
) {
    when (uiMessageIntent) {
        is LoginUiMessageIntent.EmailIsInvalid -> {
            onResetPasswordTextFieldErrorState(
                ResetPasswordTextFieldErrorState(emailIsInvalid = true)
            )
        }
    }
}

internal class ResetPasswordTextFieldErrorState(
    val emailIsInvalid: Boolean = false
)