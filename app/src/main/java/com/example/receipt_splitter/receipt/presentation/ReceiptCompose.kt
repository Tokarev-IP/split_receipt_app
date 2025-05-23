package com.example.receipt_splitter.receipt.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.receipt_splitter.main.presentation.MainEvent
import com.example.receipt_splitter.main.presentation.MainViewModel
import com.example.receipt_splitter.receipt.presentation.screens.AllReceiptsScreen
import com.example.receipt_splitter.receipt.presentation.screens.CreateReceiptScreen
import com.example.receipt_splitter.receipt.presentation.screens.EditReceiptScreen
import com.example.receipt_splitter.receipt.presentation.screens.SplitReceiptScreen
import com.example.receipt_splitter.receipt.presentation.viewmodels.AllReceiptsEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.EditReceiptEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.EditReceiptViewModel
import com.example.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptEvent
import com.example.receipt_splitter.receipt.presentation.viewmodels.SplitReceiptViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCompose(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    receiptViewModel: ReceiptViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: ReceiptNavHostDestinations = ReceiptNavHostDestinations.AllReceiptsScreenNav,
) {
    LaunchedEffect(key1 = Unit) {
        receiptViewModel.getIntentFlow().collect { receiptIntent ->
            handleReceiptIntent(
                intent = receiptIntent,
                navHostController = navHostController,
                mainViewModel = mainViewModel
            )
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = startDestination,
    ) {
        composable<ReceiptNavHostDestinations.CreateReceiptScreenNav> {
            val createReceiptViewModel: CreateReceiptViewModel = koinViewModel()
            CreateReceiptScreen(
                receiptViewModel = receiptViewModel,
                createReceiptViewModel = createReceiptViewModel
            )
        }

        composable<ReceiptNavHostDestinations.AllReceiptsScreenNav> {
            val allReceiptsViewModel: AllReceiptsViewModel = koinViewModel()
            LaunchedEffect(key1 = Unit) {
                allReceiptsViewModel.setEvent(AllReceiptsEvent.RetrieveAllReceipts)
            }
            AllReceiptsScreen(
                receiptViewModel = receiptViewModel,
                allReceiptViewModel = allReceiptsViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.SplitReceiptScreenNav> { backStackEntry ->
            val splitReceiptViewModel: SplitReceiptViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.EditReceiptScreenNav>()
            LaunchedEffect(key1 = Unit) {
                splitReceiptViewModel.setEvent(SplitReceiptEvent.RetrieveReceiptData(data.receiptId))
                splitReceiptViewModel.setEvent(SplitReceiptEvent.ActivateOrderReportCreator)
            }
            SplitReceiptScreen(
                receiptViewModel = receiptViewModel,
                splitReceiptViewModel = splitReceiptViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.EditReceiptScreenNav> { backStackEntry ->
            val editReceiptViewModel: EditReceiptViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.EditReceiptScreenNav>()
            LaunchedEffect(key1 = data.receiptId) {
                editReceiptViewModel.setEvent(EditReceiptEvent.RetrieveReceiptData(data.receiptId))
            }
            EditReceiptScreen(
                receiptViewModel = receiptViewModel,
                editReceiptViewModel = editReceiptViewModel,
            )
        }
    }
}

private fun handleReceiptIntent(
    intent: ReceiptIntent,
    navHostController: NavHostController,
    mainViewModel: MainViewModel,
) {
    when (intent) {
        is ReceiptIntent.GoToSplitReceiptScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.SplitReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.CreateReceiptScreenNav> {
                    inclusive = true
                    saveState
                }
                launchSingleTop = true
            }
        }

        is ReceiptIntent.GoToCreateReceiptScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.CreateReceiptScreenNav)
        }

        is ReceiptIntent.GoToEditReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.CreateReceiptScreenNav> {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        is ReceiptIntent.GoToAllReceiptsScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.AllReceiptsScreenNav)
        }

        is ReceiptIntent.GoBackNavigation -> {
            navHostController.popBackStack()
        }

        is ReceiptIntent.GoToSettings -> {
            mainViewModel.setEvent(MainEvent.OpenSettings)
        }
    }
}