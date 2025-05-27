package com.iliatokarev.receipt_splitter.login.presentation.screens

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
import com.iliatokarev.receipt_splitter.R
import com.iliatokarev.receipt_splitter.login.presentation.LoginEvent
import com.iliatokarev.receipt_splitter.login.presentation.LoginNavigationEvent
import com.iliatokarev.receipt_splitter.login.presentation.LoginUiMessageIntent
import com.iliatokarev.receipt_splitter.login.presentation.LoginUiState
import com.iliatokarev.receipt_splitter.login.presentation.LoginViewModel
import com.iliatokarev.receipt_splitter.login.presentation.views.RegistrationScreenView
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
) {
    val uiState by loginViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var registrationTextFieldErrorState by remember {
        mutableStateOf(
            RegistrationTextFieldErrorState()
        )
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow().collect { uiMessageIntent ->
            handleUiMessages(
                uiMessageIntent = uiMessageIntent,
                onRegistrationTextFieldErrorState = { state ->
                    registrationTextFieldErrorState = state
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.registration)) },
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

        RegistrationScreenView(
            modifier = modifier.padding(innerPadding),
            enabled = { uiState is LoginUiState.Show },
            textFieldErrorState = { registrationTextFieldErrorState },
            onTextFieldErrorState = { state -> registrationTextFieldErrorState = state },
            onCreateAccountClick = { email, password ->
                loginViewModel.setEvent(
                    LoginEvent.CreateNewAccount(email = email, password = password)
                )
            }
        )
    }
}

private fun handleUiMessages(
    uiMessageIntent: LoginUiMessageIntent,
    onRegistrationTextFieldErrorState: (RegistrationTextFieldErrorState) -> Unit,
) {
    when (uiMessageIntent) {
        is LoginUiMessageIntent.EmailIsInvalid -> {
            onRegistrationTextFieldErrorState(
                RegistrationTextFieldErrorState(emailIsInvalid = true)
            )
        }

        is LoginUiMessageIntent.PasswordIsInvalid -> {
            onRegistrationTextFieldErrorState(
                RegistrationTextFieldErrorState(passwordIsInvalid = true)
            )
        }

        is LoginUiMessageIntent.EmailIsUsedByAnotherAccount -> {
            onRegistrationTextFieldErrorState(
                RegistrationTextFieldErrorState(emailIsUsed = true)
            )
        }
    }
}

@Serializable
internal class RegistrationTextFieldErrorState(
    val emailIsInvalid: Boolean = false,
    val emailIsIncorrect: Boolean = false,
    val emailIsUsed: Boolean = false,
    val passwordIsInvalid: Boolean = false,
    val passwordIsIncorrect: Boolean = false,
    val confirmPasswordIsIncorrect: Boolean = false,
)