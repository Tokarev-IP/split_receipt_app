package com.iliatokarev.receipt_splitter_app.login.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.login.presentation.screens.SignInTextFieldErrorState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEmailTextField
import com.iliatokarev.receipt_splitter_app.main.basic.BasicPasswordTextField
import com.iliatokarev.receipt_splitter_app.main.basic.isCorrectPassword
import com.iliatokarev.receipt_splitter_app.main.basic.isEmail

@Composable
internal fun SignInScreenView(
    modifier: Modifier = Modifier,
    onEmailAndPasswordSignInClick: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleSignInClick: () -> Unit = {},
    onRegistrationClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    enabled: () -> Boolean,
    textFieldErrors: () -> SignInTextFieldErrorState = { SignInTextFieldErrorState() },
    onTextFieldErrorStateChanged: (SignInTextFieldErrorState) -> Unit = {},
    emailText: () -> String = { "" },
    passwordText: () -> String = { "" },
    onEmailTextChanged: (String) -> Unit = {},
    onPasswordTextChanged: (String) -> Unit = {},
    onGetSavedCredentialClick: () -> Unit = {},
    versionText: String = "",
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            SignInView(
                onEmailAndPasswordSignInClick = { email, password ->
                    onEmailAndPasswordSignInClick(email, password)
                },
                onGoogleSignInClick = { onGoogleSignInClick() },
                enabled = { enabled() },
                onRegistrationClick = { onRegistrationClick() },
                onForgotPasswordClick = { onForgotPasswordClick() },
                textFieldErrors = { textFieldErrors() },
                onTextFieldErrorStateChanged = { state: SignInTextFieldErrorState ->
                    onTextFieldErrorStateChanged(state)
                },
                emailText = { emailText() },
                passwordText = { passwordText() },
                onEmailTextChanged = { value: String ->
                    onEmailTextChanged(value)
                },
                onPasswordTextChanged = { value: String ->
                    onPasswordTextChanged(value)
                },
                onGetSavedCredentialClick = { onGetSavedCredentialClick() },
                versionText = versionText,
            )
        }
    }
}

@Composable
private fun SignInView(
    modifier: Modifier = Modifier,
    onEmailAndPasswordSignInClick: (email: String, password: String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    enabled: () -> Boolean,
    onRegistrationClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    textFieldErrors: () -> SignInTextFieldErrorState,
    onTextFieldErrorStateChanged: (SignInTextFieldErrorState) -> Unit,
    emailText: () -> String,
    passwordText: () -> String,
    onEmailTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
    onGetSavedCredentialClick: () -> Unit,
    versionText: String,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BasicEmailTextField(
            emailText = { emailText() },
            onEmailTextChanged = { value: String ->
                onEmailTextChanged(value.trim())
            },
            emailTextFieldErrorState = {
                textFieldErrors().emailIsInvalid || textFieldErrors().emailIsIncorrect
            },
            onEmailTextFieldErrorStateChanged = { state: Boolean ->
                onTextFieldErrorStateChanged(
                    SignInTextFieldErrorState(
                        emailIsInvalid = state,
                        emailIsIncorrect = state,
                    )
                )
            },
            enabled = { enabled() },
            labelText = stringResource(R.string.email),
            errorText =
                if (textFieldErrors().emailIsInvalid) stringResource(R.string.email_is_invalid)
                else if (textFieldErrors().emailIsIncorrect) stringResource(R.string.email_is_incorrect)
                else stringResource(R.string.email_is_incorrect),
            leadingIcon = {
                IconButton(
                    onClick = { onGetSavedCredentialClick() },
                ) {
                    Icon(Icons.Filled.AccountCircle, stringResource(R.string.credential_button))
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        BasicPasswordTextField(
            passwordText = { passwordText() },
            onPasswordTextChanged = { value: String ->
                onPasswordTextChanged(value.trim())
            },
            passwordTextFieldErrorState = {
                textFieldErrors().passwordIsIncorrect || textFieldErrors().passwordIsInvalid
            },
            onPasswordTextFieldErrorStateChanged = { state: Boolean ->
                onTextFieldErrorStateChanged(
                    SignInTextFieldErrorState(
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
                if (textFieldErrors().passwordIsInvalid) stringResource(R.string.password_is_invalid)
                else if (textFieldErrors().passwordIsIncorrect) stringResource(R.string.password_is_incorrect)
                else stringResource(R.string.password_is_incorrect),
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = {
                if (emailText().isEmail()) {
                    if (passwordText().isCorrectPassword())
                        onEmailAndPasswordSignInClick(emailText(), passwordText())
                    else onTextFieldErrorStateChanged(SignInTextFieldErrorState(passwordIsInvalid = true))
                } else
                    onTextFieldErrorStateChanged(SignInTextFieldErrorState(emailIsInvalid = true))
            },
            enabled = enabled(),
        ) {
            Text(text = stringResource(R.string.sign_in_with_email_and_password))
        }

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = { onGoogleSignInClick() },
            enabled = enabled(),
        ) {
            if (enabled())
                Image(
                    painter = painterResource(id = com.google.firebase.appcheck.interop.R.drawable.googleg_standard_color_18),
                    contentDescription = "null",
                )
            else
                Image(
                    painter = painterResource(id = com.google.firebase.appcheck.interop.R.drawable.googleg_disabled_color_18),
                    contentDescription = "null",
                )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(R.string.sign_in_with_google))
        }

        Spacer(modifier = Modifier.height(40.dp))
        RegistrationAndForgotPasswordView(
            onRegistrationClick = { onRegistrationClick() },
            onForgotPasswordClick = { onForgotPasswordClick() },
            enabled = { enabled() },
        )

        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = versionText,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun RegistrationAndForgotPasswordView(
    modifier: Modifier = Modifier,
    enabled: () -> Boolean,
    onRegistrationClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { onRegistrationClick() },
            enabled = enabled(),
        ) {
            Text(
                text = stringResource(R.string.register),
                fontWeight = FontWeight.Medium,
            )
        }
        TextButton(
            onClick = { onForgotPasswordClick() },
            enabled = enabled(),
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SignInViewPreview() {
    SignInScreenView(
        enabled = { true },
    )
}