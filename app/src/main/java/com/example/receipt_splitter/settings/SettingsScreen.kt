package com.example.receipt_splitter.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receipt_splitter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            settingsViewModel.setEvent(SettingsEvent.GoBackNavigation)
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
        SettingsScreenView(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            onSignOut = {
                settingsViewModel.setEvent(SettingsEvent.SignOut)
            }
        )
    }
}

@Composable
private fun SettingsScreenView(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        SettingsView(
            onSignOut = { onSignOut() }
        )
    }
}

@Composable
private fun SettingsView(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {

        ElevatedButton(
            modifier = modifier.align(Alignment.Center),
            onClick = { onSignOut() }
        ) {
            Text(text = "Sign Out")
        }

        Text(
            modifier = modifier.align(Alignment.BottomCenter),
            fontSize = 12.sp,
            text = "version 1.0.0 made by Ilia T.",
        )
    }
}