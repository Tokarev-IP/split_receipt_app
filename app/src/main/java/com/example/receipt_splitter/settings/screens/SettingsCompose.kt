package com.example.receipt_splitter.settings.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receipt_splitter.R
import com.example.receipt_splitter.main.presentation.MainEvent
import com.example.receipt_splitter.main.presentation.MainViewModel
import com.example.receipt_splitter.settings.SettingsIntent
import com.example.receipt_splitter.settings.SettingsUiMessageIntent
import com.example.receipt_splitter.settings.SettingsUiMessages
import com.example.receipt_splitter.settings.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable

@Composable
internal fun SettingsCompose(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: SettingsNavHostDestinations = SettingsNavHostDestinations.SettingsScreenNav,
    myContext: Context = LocalContext.current,
) {
    val messageUiMap = mapOf<String, String>(
        SettingsUiMessages.INTERNAL_ERROR.message to stringResource(R.string.internal_error),
        SettingsUiMessages.ACCOUNT_WAS_DELETED.message to stringResource(R.string.account_was_deleted),
        SettingsUiMessages.USER_IS_EMPTY.message to stringResource(R.string.sign_in_required),
    )

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.getUiMessageIntentFlow().collectLatest {
            handleUiMessages(it, messageUiMap, myContext)
        }
    }

    LaunchedEffect(key1 = Unit) {
        settingsViewModel.getIntentFlow().collectLatest { intent ->
            when (intent) {
                is SettingsIntent.SignOut -> {
                    mainViewModel.setEvent(MainEvent.UserIsSignedOut)
                }

                is SettingsIntent.GoBackToReceipt -> {
                    mainViewModel.setEvent(MainEvent.GoBackNavigation)
                }
            }
        }
    }

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable<SettingsNavHostDestinations.SettingsScreenNav> {
            SettingsScreen(settingsViewModel = settingsViewModel)
        }
    }

}

sealed interface SettingsNavHostDestinations {
    @Serializable
    object SettingsScreenNav : SettingsNavHostDestinations
}

private fun handleUiMessages(
    settingsUiMessageIntent: SettingsUiMessageIntent,
    messageUiMap: Map<String, String>,
    context: Context,
) {
    when (settingsUiMessageIntent) {
        is SettingsUiMessageIntent.AccountWasDeleted -> {
            Toast.makeText(
                context,
                messageUiMap[SettingsUiMessages.ACCOUNT_WAS_DELETED.message]
                    ?: SettingsUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_LONG
            ).show()
        }

        is SettingsUiMessageIntent.EmptyUser -> {
            Toast.makeText(
                context,
                messageUiMap[SettingsUiMessages.USER_IS_EMPTY.message]
                    ?: SettingsUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_LONG
            ).show()
        }

        is SettingsUiMessageIntent.InternalError -> {
            Toast.makeText(
                context,
                messageUiMap[SettingsUiMessages.INTERNAL_ERROR.message]
                    ?: SettingsUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}