package com.iliatokarev.receipt_splitter_app.main.basic

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.main.basic.icons.Visibility
import com.iliatokarev.receipt_splitter_app.main.basic.icons.VisibilityOff

private const val EMPTY_STRING = ""
private const val MAX_PASSWORD_LENGTH = 45
private const val MAX_EMAIL_LENGTH = 70
private const val MAX_LINES = 1

@Composable
fun BasicEmailTextField(
    modifier: Modifier = Modifier,
    emailText: () -> String,
    onEmailTextChanged: (String) -> Unit,
    emailTextFieldErrorState: () -> Boolean,
    onEmailTextFieldErrorStateChanged: (Boolean) -> Unit,
    enabled: () -> Boolean,
    labelText: String,
    errorText: String,
    supportingText: String? = null,
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        maxLines = MAX_LINES,
        value = emailText(),
        onValueChange = { value: String ->
            onEmailTextFieldErrorStateChanged(false)
            if (value.trim().length < MAX_EMAIL_LENGTH)
                onEmailTextChanged(value.trim())
        },
        trailingIcon = {
            if (emailText().isNotEmpty())
                IconButton(onClick = { onEmailTextChanged(EMPTY_STRING) }) {
                    Icon(Icons.Filled.Clear, stringResource(R.string.clear_email_button))
                }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text(text = labelText) },
        enabled = enabled(),
        supportingText = {
            if (emailTextFieldErrorState())
                Text(text = errorText)
            else {
                supportingText?.let { Text(text = supportingText) }
            }
        },
        isError = emailTextFieldErrorState(),
        interactionSource = interactionSource,
        leadingIcon = leadingIcon
    )
}

@Composable
fun BasicPasswordTextField(
    modifier: Modifier = Modifier,
    passwordText: () -> String,
    onPasswordTextChanged: (String) -> Unit,
    passwordTextFieldErrorState: () -> Boolean,
    onPasswordTextFieldErrorStateChanged: (Boolean) -> Unit,
    showPassword: () -> Boolean,
    onShowPasswordChanged: (Boolean) -> Unit,
    enabled: () -> Boolean,
    labelText: String,
    errorText: String,
    supportingText: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        maxLines = MAX_LINES,
        value = passwordText(),
        onValueChange = { value: String ->
            onPasswordTextFieldErrorStateChanged(false)
            if (value.trim().length < MAX_PASSWORD_LENGTH)
                onPasswordTextChanged(value.trim())
        },
        trailingIcon = {
            if (passwordText().isNotEmpty())
                IconButton(onClick = { onPasswordTextChanged(EMPTY_STRING) }) {
                    Icon(Icons.Filled.Clear, stringResource(R.string.clear_password_button))
                }
        },
        leadingIcon = {
            IconButton(
                onClick = { onShowPasswordChanged(!showPassword()) }
            ) {
                AnimatedContent(
                    targetState = showPassword()
                ) { show ->
                    if (show)
                        Icon(Icons.Filled.VisibilityOff, stringResource(R.string.hide_password))
                    else
                        Icon(Icons.Filled.Visibility, stringResource(R.string.show_password))
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        label = { Text(text = labelText) },
        enabled = enabled(),
        visualTransformation = if (showPassword()) VisualTransformation.None else PasswordVisualTransformation(),
        supportingText = {
            if (passwordTextFieldErrorState())
                Text(text = errorText)
            else {
                supportingText?.let { Text(text = supportingText) }
            }
        },
        isError = passwordTextFieldErrorState(),
        interactionSource = interactionSource,
    )
}