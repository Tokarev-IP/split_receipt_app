package com.iliatokarev.receipt_splitter_app.receipt.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.iliatokarev.receipt_splitter_app.main.presentation.MainEvent
import com.iliatokarev.receipt_splitter_app.main.presentation.MainViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.AllReceiptsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.CreateReceiptScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.EditReceiptScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.SplitReceiptScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllEvents
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCompose(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    receiptViewModel: ReceiptViewModel,
    navHostController: NavHostController = rememberNavController(),
    startDestination: ReceiptNavHostDestinations = ReceiptNavHostDestinations.AllReceiptsScreenNav,
    context: Context = LocalContext.current,
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

    LaunchedEffect(key1 = Unit) {
        receiptViewModel.getUiMessageIntentFlow().collect { uiMessageIntent ->
            when (uiMessageIntent) {
                is ReceiptUiMessageIntent.UiMessage -> {
                    Toast.makeText(
                        context,
                        uiMessageIntent.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
            val splitReceiptForOneViewModel: SplitReceiptForOneViewModel = koinViewModel()
            val splitReceiptForAllViewModel: SplitReceiptForAllViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.EditReceiptScreenNav>()
            LaunchedEffect(key1 = Unit) {
                splitReceiptForOneViewModel.setEvent(
                    SplitReceiptForOneEvent.RetrieveReceiptData(
                        data.receiptId
                    )
                )
                splitReceiptForOneViewModel.setEvent(SplitReceiptForOneEvent.ActivateOrderReportCreator)
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.RetrieveReceiptData(
                        data.receiptId
                    )
                )
                splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ActivateOrderReportCreator)
            }
            SplitReceiptScreen(
                receiptViewModel = receiptViewModel,
                splitReceiptForOneViewModel = splitReceiptForOneViewModel,
                splitReceiptForAllViewModel = splitReceiptForAllViewModel,
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
                popUpTo<ReceiptNavHostDestinations.EditReceiptScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.GoToCreateReceiptScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.CreateReceiptScreenNav)
        }

        is ReceiptIntent.GoToEditReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.SplitReceiptScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.NewReceiptIsCreated -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.CreateReceiptScreenNav> {
                    inclusive = true
                }
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

        is ReceiptIntent.UserIsEmpty -> {
            mainViewModel.setEvent(MainEvent.UserIsSignedOut)
        }
    }
}