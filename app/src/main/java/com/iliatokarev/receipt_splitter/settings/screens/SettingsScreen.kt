package com.iliatokarev.receipt_splitter.settings.screens

import android.content.Context
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter.R
import com.iliatokarev.receipt_splitter.main.basic.getAppVersion
import com.iliatokarev.receipt_splitter.settings.SettingsEvent
import com.iliatokarev.receipt_splitter.settings.SettingsUiState
import com.iliatokarev.receipt_splitter.settings.SettingsViewModel
import com.iliatokarev.receipt_splitter.settings.dialogs.DeleteAccountDialog
import com.iliatokarev.receipt_splitter.settings.dialogs.SignOutDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    myContext: Context = LocalContext.current
) {
    val version = remember { getAppVersion(myContext) }
    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by rememberSaveable { mutableStateOf(false) }

    val uiState by settingsViewModel.getUiStateFlow().collectAsStateWithLifecycle()

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
        if (uiState is SettingsUiState.Loading) {
            LinearProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            )
        }

        SettingsScreenView(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            onSignOut = {
                showSignOutDialog = true
            },
            onDeleteUserAccount = {
                showDeleteAccountDialog = true
            },
            appVersion = version,
            enabled = uiState is SettingsUiState.Show
        )

        if (showSignOutDialog) {
            SignOutDialog(
                onDismissRequest = { showSignOutDialog = false },
                onSignOutClicked = {
                    showSignOutDialog = false
                    settingsViewModel.setEvent(SettingsEvent.SignOut)
                },
            )
        }

        if (showDeleteAccountDialog) {
            DeleteAccountDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                onAcceptClicked = {
                    showDeleteAccountDialog = false
                    settingsViewModel.setEvent(SettingsEvent.DeleteUserAccount)
                }
            )
        }
    }
}