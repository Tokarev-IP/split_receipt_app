package com.example.receipt_splitter.receipt.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receipt_splitter.main.presentation.MainViewModel
import com.example.receipt_splitter.receipt.presentation.screens.ChoosePhotoScreen
import com.example.receipt_splitter.receipt.presentation.screens.ShowReceiptsScreen
import com.example.receipt_splitter.receipt.presentation.screens.SplitReceiptScreen

@Composable
fun ReceiptCompose(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    receiptViewModel: ReceiptViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: ReceiptNavHostDestinations = ReceiptNavHostDestinations.ShowReceiptsScreenNav,
) {
    val intent by receiptViewModel.getIntentFlow().collectAsState(initial = null)

    LaunchedEffect(key1 = Unit) {
        receiptViewModel.setUiEvent(ReceiptEvent.RetrieveAllReceipts)
    }

    when (intent) {
        is ReceiptIntent.GoToSplitReceiptScreen -> {
            receiptViewModel.clearIntentFlow()
            navHostController.navigate(ReceiptNavHostDestinations.SplitReceiptScreenNav)
            receiptViewModel.setUiEvent(ReceiptEvent.SetShowState)
        }

        is ReceiptIntent.GoToChoosePhotoScreen -> {
            receiptViewModel.clearIntentFlow()
            navHostController.navigate(ReceiptNavHostDestinations.ChoosePhotoScreenNav)
            receiptViewModel.setUiEvent(ReceiptEvent.SetShowState)
        }

        is ReceiptIntent.GoToShowReceiptsScreen -> {
            receiptViewModel.clearIntentFlow()
            navHostController.navigate(ReceiptNavHostDestinations.ShowReceiptsScreenNav)
            receiptViewModel.setUiEvent(ReceiptEvent.SetShowState)
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = startDestination,
    ) {
        composable<ReceiptNavHostDestinations.ChoosePhotoScreenNav> {
            ChoosePhotoScreen(receiptViewModel = receiptViewModel)
        }

        composable<ReceiptNavHostDestinations.ShowReceiptsScreenNav> {
            ShowReceiptsScreen(receiptViewModel = receiptViewModel)
        }

        composable<ReceiptNavHostDestinations.SplitReceiptScreenNav> {
            SplitReceiptScreen(receiptViewModel = receiptViewModel)
        }
    }
}