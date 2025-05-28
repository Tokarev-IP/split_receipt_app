package com.iliatokarev.receipt_splitter_app.login.presentation.views

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.login.presentation.screens.RegistrationTextFieldErrorState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEmailTextField
import com.iliatokarev.receipt_splitter_app.main.basic.BasicPasswordTextField
import com.iliatokarev.receipt_splitter_app.main.basic.isCorrectPassword
import com.iliatokarev.receipt_splitter_app.main.basic.isEmail

@Composable
internal fun RegistrationScreenView(
    modifier: Modifier = Modifier,
    enabled: () -> Boolean = { true },
    textFieldErrorState: () -> RegistrationTextFieldErrorState = { RegistrationTextFieldErrorState() },
    onTextFieldErrorState: (RegistrationTextFieldErrorState) -> Unit = {},
    onCreateAccountClick: (email: String, password: String) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        RegistrationView(
            enabled = { enabled() },
            textFieldErrorState = { textFieldErrorState() },
            onTextFieldErrorState = { state -> onTextFieldErrorState(state) },
            onCreateAccountClick = { email, password ->
                onCreateAccountClick(email, password)
            }
        )
    }
}

@Composable
private fun RegistrationView(
    modifier: Modifier = Modifier,
    enabled: () -> Boolean,
    textFieldErrorState: () -> RegistrationTextFieldErrorState,
    onTextFieldErrorState: (RegistrationTextFieldErrorState) -> Unit,
    onCreateAccountClick: (email: String, password: String) -> Unit,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }
    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var confirmedPasswordText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicEmailTextField(
            emailText = { emailText },
            onEmailTextChanged = { value: String ->
                emailText = value
            },
            onEmailTextFieldErrorStateChanged = { state: Boolean ->
                onTextFieldErrorState(
                    RegistrationTextFieldErrorState(
                        emailIsInvalid = state,
                        emailIsUsed = state,
                        emailIsIncorrect = state,
                    )
                )
            },
            emailTextFieldErrorState = {
                textFieldErrorState().emailIsUsed || textFieldErrorState().emailIsIncorrect || textFieldErrorState().emailIsInvalid
            },
            enabled = { enabled() },
            labelText = stringResource(R.string.email),
            errorText =
                if (textFieldErrorState().emailIsInvalid) stringResource(R.string.email_is_invalid)
                else if (textFieldErrorState().emailIsUsed) stringResource(R.string.email_is_used)
                else if (textFieldErrorState().emailIsIncorrect) stringResource(R.string.email_is_incorrect)
                else stringResource(R.string.email_is_invalid),
        )

        Spacer(modifier = modifier.height(20.dp))

        BasicPasswordTextField(
            passwordText = { passwordText },
            onPasswordTextChanged = { value: String ->
                passwordText = value
            },
            passwordTextFieldErrorState = {
                textFieldErrorState().passwordIsInvalid || textFieldErrorState().passwordIsIncorrect
            },
            onPasswordTextFieldErrorStateChanged = { state: Boolean ->
                onTextFieldErrorState(
                    RegistrationTextFieldErrorState(
                        passwordIsInvalid = state,
                        passwordIsIncorrect = state,
                    )
                )
            },
            showPassword = { showPassword },
            onShowPasswordChanged = { state: Boolean ->
                showPassword = state
            },
            enabled = { enabled() },
            labelText = stringResource(R.string.password),
            errorText =
                if (textFieldErrorState().passwordIsInvalid) stringResource(R.string.password_is_invalid)
                else if (textFieldErrorState().passwordIsIncorrect) stringResource(R.string.password_is_incorrect)
                else stringResource(R.string.password_is_invalid),
            supportingText = stringResource(R.string.password_must_include),
        )

        Spacer(modifier = modifier.height(16.dp))
        BasicPasswordTextField(
            passwordText = { confirmedPasswordText },
            onPasswordTextChanged = { value: String ->
                confirmedPasswordText = value
            },
            passwordTextFieldErrorState = {
                textFieldErrorState().confirmPasswordIsIncorrect
            },
            onPasswordTextFieldErrorStateChanged = { state: Boolean ->
                onTextFieldErrorState(RegistrationTextFieldErrorState(confirmPasswordIsIncorrect = state))
            },
            showPassword = { showConfirmPassword },
            onShowPasswordChanged = { state: Boolean ->
                showConfirmPassword = state
            },
            enabled = { enabled() },
            labelText = stringResource(R.string.confirm_password),
            errorText =
                if (textFieldErrorState().confirmPasswordIsIncorrect) stringResource(R.string.password_repeat_is_incorrect)
                else stringResource(R.string.password_repeat_is_incorrect),
        )

        Spacer(modifier = modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                if (emailText.isEmail()) {
                    if (passwordText == confirmedPasswordText) {
                        if (passwordText.isCorrectPassword()) {
                            onCreateAccountClick(emailText, passwordText)
                        } else
                            onTextFieldErrorState(RegistrationTextFieldErrorState(passwordIsInvalid = true))
                    } else
                        onTextFieldErrorState(
                            RegistrationTextFieldErrorState(confirmPasswordIsIncorrect = true)
                        )
                } else
                    onTextFieldErrorState(RegistrationTextFieldErrorState(emailIsInvalid = true))
            },
            enabled = enabled(),
        ) {
            Text(text = stringResource(R.string.create_new_account))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RegistrationScreenViewPreview() {
    RegistrationScreenView()
}