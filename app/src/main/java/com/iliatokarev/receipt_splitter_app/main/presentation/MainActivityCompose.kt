package com.iliatokarev.receipt_splitter_app.main.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginCompose
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptCompose
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptViewModel
import com.iliatokarev.receipt_splitter_app.settings.SettingsViewModel
import com.iliatokarev.receipt_splitter_app.settings.screens.SettingsCompose
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainActivityCompose(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: MainNavHostDestinations = MainNavHostDestinations.LoginNav,
    mainViewModel: MainViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        mainViewModel.getIntentFlow().collect { mainIntent ->
            handleMainIntent(
                intent = mainIntent,
                navHostController = navHostController
            )
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = startDestination,
    ) {
        composable<MainNavHostDestinations.LoginNav> {
            val loginViewModel: LoginViewModel = koinViewModel()
            LoginCompose(
                loginViewModel = loginViewModel,
                mainViewModel = mainViewModel,
            )
        }

        composable<MainNavHostDestinations.ReceiptNav> {
            val receiptViewModel: ReceiptViewModel = koinViewModel()
            ReceiptCompose(
                mainViewModel = mainViewModel,
                receiptViewModel = receiptViewModel,
            )
        }

        composable<MainNavHostDestinations.SettingsNav> {
            val settingsViewModel: SettingsViewModel = koinViewModel()
            SettingsCompose(
                settingsViewModel = settingsViewModel,
                mainViewModel = mainViewModel,
            )
        }
    }
}

private fun handleMainIntent(
    intent: MainIntent,
    navHostController: NavHostController,
) {
    when (intent) {
        is MainIntent.GoToReceiptScreen -> {
            navHostController.navigate(MainNavHostDestinations.ReceiptNav) {
                popUpTo<MainNavHostDestinations.LoginNav> {
                    inclusive = true
                }
            }
        }

        is MainIntent.GoToLoginScreen -> {
            navHostController.navigate(MainNavHostDestinations.LoginNav) {
                popUpTo<MainNavHostDestinations.ReceiptNav> {
                    inclusive = true
                }
            }
        }

        is MainIntent.GoToSettingsScreen -> {
            navHostController.navigate(MainNavHostDestinations.SettingsNav)
        }

        is MainIntent.GoBackNavigation -> {
            navHostController.popBackStack()
        }
    }
}