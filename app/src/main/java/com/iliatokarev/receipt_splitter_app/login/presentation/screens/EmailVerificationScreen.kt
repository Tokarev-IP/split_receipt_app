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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginNavigationEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiState
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginViewModel
import com.iliatokarev.receipt_splitter_app.login.presentation.views.EmailVerificationScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    email: String,
    password: String,
) {
    val uiState by loginViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.email_verification)) },
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
        }

        EmailVerificationScreenView(
            modifier = modifier.padding(innerPadding),
            email = email,
            onConfirmEmailVerificationClick = {
                loginViewModel.setEvent(LoginEvent.ConfirmEmailVerification(email, password))
            },
            onResendVerificationEmailClick = {
                loginViewModel.setEvent(LoginEvent.SendVerificationEmail)
            },
            enabled = { uiState is LoginUiState.Show }
        )
    }
}