package com.example.receipt_splitter.main.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receipt_splitter.login.presentation.LoginCompose
import com.example.receipt_splitter.login.presentation.LoginViewModel
import com.example.receipt_splitter.receipt.presentation.ReceiptCompose
import com.example.receipt_splitter.receipt.presentation.ReceiptViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainActivityCompose(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: MainNavHostDestinations = MainNavHostDestinations.LoginNav,
    mainViewModel: MainViewModel = koinViewModel(),
) {
    LaunchedEffect(key1 = Unit) {
        mainViewModel.getIntentFlow().collect { mainIntent ->
            mainIntent?.let { intent ->
                mainViewModel.clearIntentFlow()
                when (intent) {
                    is MainIntent.GoToReceiptScreen -> {
                        navHostController.navigate(MainNavHostDestinations.ReceiptNav){
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    is MainIntent.GoToLoginScreen -> {
                        navHostController.navigate(MainNavHostDestinations.LoginNav){
                            popUpTo(navHostController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            }
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
    }
}