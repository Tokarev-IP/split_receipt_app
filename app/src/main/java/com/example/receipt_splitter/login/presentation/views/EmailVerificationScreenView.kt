package com.example.receipt_splitter.login.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.receipt_splitter.R

@Composable
internal fun EmailVerificationScreenView(
    modifier: Modifier = Modifier,
    email: String,
    enabled: () -> Boolean,
    onConfirmEmailVerificationClick: () -> Unit = {},
    onResendVerificationEmailClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmailVerificationView(
            email = email,
            enabled = { enabled() },
            onConfirmEmailVerificationClick = { onConfirmEmailVerificationClick() },
            onResendVerificationEmailClick = { onResendVerificationEmailClick() },
        )
    }
}

@Composable
private fun EmailVerificationView(
    modifier: Modifier = Modifier,
    email: String,
    onResendVerificationEmailClick: () -> Unit = {},
    onConfirmEmailVerificationClick: () -> Unit = {},
    enabled: () -> Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.email_verification_message_was_sent_to_address, email),
            textAlign = TextAlign.Center,
        )
        Text(
            fontWeight = FontWeight.SemiBold,
            text = email,
        )

        Spacer(modifier = modifier.height(40.dp))
        Text(text = stringResource(R.string.go_to_this_address_to_verify_your_email))

        Spacer(modifier = modifier.height(60.dp))
        Button(
            onClick = { onConfirmEmailVerificationClick() },
            enabled = enabled(),
        ) {
            Text(text = stringResource(R.string.continue_with_verified_email))
        }

        Spacer(modifier = modifier.height(20.dp))
        TextButton(
            onClick = { onResendVerificationEmailClick() },
            enabled = enabled(),
        ) {
            Text(text = stringResource(R.string.re_send_verification_email))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EmailVerificationScreenViewPreview() {
    EmailVerificationScreenView(
        email = "testemail@text.com",
        enabled = { true },
    )
}