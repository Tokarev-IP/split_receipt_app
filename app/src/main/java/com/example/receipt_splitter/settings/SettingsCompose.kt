package com.example.receipt_splitter.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receipt_splitter.main.presentation.MainEvent
import com.example.receipt_splitter.main.presentation.MainViewModel
import kotlinx.serialization.Serializable

@Composable
internal fun SettingsCompose(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: SettingsNavHostDestinations = SettingsNavHostDestinations.SettingsScreenNav,
) {
    LaunchedEffect(key1 = Unit) {
        settingsViewModel.getIntentFlow().collect { intent ->
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