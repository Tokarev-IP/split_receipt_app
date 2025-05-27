package com.example.receipt_splitter.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R

@Composable
internal fun SettingsScreenView(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    onDeleteUserAccount: () -> Unit,
    appVersion: String,
    enabled: Boolean,
) {
    Box(modifier = modifier.fillMaxSize()) {
        SettingsView(
            onSignOut = { onSignOut() },
            onDeleteUserAccount = { onDeleteUserAccount() },
            appVersion = appVersion,
            enabled = enabled,
        )
    }
}

@Composable
private fun SettingsView(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
    onDeleteUserAccount: () -> Unit,
    appVersion: String,
    enabled: Boolean = true,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            ElevatedButton(
                onClick = { onSignOut() },
                enabled = enabled,
            ) {
                Text(text = stringResource(R.string.sign_out))
            }
            Spacer(modifier = Modifier.height(100.dp))

            TextButton(
                onClick = { onDeleteUserAccount() },
                enabled = enabled,
            ) {
                Text(text = stringResource(R.string.delete_user_account))
            }
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                fontSize = 12.sp,
                text = stringResource(R.string.version_of_app, appVersion),
            )
        }
    }
}