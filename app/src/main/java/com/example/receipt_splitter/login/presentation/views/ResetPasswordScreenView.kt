package com.example.receipt_splitter.login.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.receipt_splitter.R
import com.example.receipt_splitter.login.presentation.screens.ResetPasswordTextFieldErrorState
import com.example.receipt_splitter.main.basic.BasicEmailTextField
import com.example.receipt_splitter.main.basic.isEmail

@Composable
internal fun ResetPasswordScreenView(
    modifier: Modifier = Modifier,
    enabled: () -> Boolean,
    onResetPasswordClick: (email: String) -> Unit,
    resetPasswordFieldErrorState: () -> ResetPasswordTextFieldErrorState,
    onResetPasswordFieldState: (ResetPasswordTextFieldErrorState) -> Unit,
) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ResetPasswordView(
            enabled = { enabled() },
            onResetPasswordClick = { email -> onResetPasswordClick(email) },
            resetPasswordFieldErrorState = { resetPasswordFieldErrorState() },
            onResetPasswordFieldState = { state -> onResetPasswordFieldState(state) }
        )
    }
}

@Composable
private fun ResetPasswordView(
    modifier: Modifier = Modifier,
    enabled: () -> Boolean,
    onResetPasswordClick: (email: String) -> Unit,
    resetPasswordFieldErrorState: () -> ResetPasswordTextFieldErrorState,
    onResetPasswordFieldState: (ResetPasswordTextFieldErrorState) -> Unit,
) {
    var emailText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.reset_password_screen_text)
        )

        Spacer(modifier = modifier.height(20.dp))
        BasicEmailTextField(
            emailText = { emailText },
            onEmailTextChanged = { text: String -> emailText = text },
            emailTextFieldErrorState = { resetPasswordFieldErrorState().emailIsInvalid },
            onEmailTextFieldErrorStateChanged = { state ->
                onResetPasswordFieldState(ResetPasswordTextFieldErrorState(emailIsInvalid = state))
            },
            enabled = { enabled() },
            labelText = stringResource(R.string.email),
            errorText =
                if (resetPasswordFieldErrorState().emailIsInvalid) stringResource(R.string.email_is_invalid)
                else stringResource(R.string.email_is_incorrect)
        )

        Spacer(modifier = modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                if (emailText.isEmail())
                    onResetPasswordClick(emailText)
                else
                    onResetPasswordFieldState(ResetPasswordTextFieldErrorState(emailIsInvalid = true))
            },
            enabled = enabled(),
        ) {
            Text(text = stringResource(R.string.send_reset_password_email))
        }
    }
}