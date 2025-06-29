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
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.FolderReceiptScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.ShowReportsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.SplitReceiptForAllScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.screens.SplitReceiptForOneScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.AllReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.CreateReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.EditReceiptViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.FolderReceiptsViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.ReceiptReports
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllEvents
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForAllViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneEvent
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.SplitReceiptForOneViewModel
import kotlinx.serialization.Serializable
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
        composable<ReceiptNavHostDestinations.CreateReceiptScreenNav> { backStackEntry ->
            val createReceiptViewModel: CreateReceiptViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.CreateReceiptScreenNav>()
            CreateReceiptScreen(
                receiptViewModel = receiptViewModel,
                createReceiptViewModel = createReceiptViewModel,
                folderId = data.folderId,
            )
        }

        composable<ReceiptNavHostDestinations.AllReceiptsScreenNav> {
            val allReceiptsViewModel: AllReceiptsViewModel = koinViewModel()
            LaunchedEffect(key1 = Unit) {
                allReceiptsViewModel.setEvent(AllReceiptsEvent.RetrieveAllData)
            }
            AllReceiptsScreen(
                receiptViewModel = receiptViewModel,
                allReceiptsViewModel = allReceiptsViewModel,
            )
        }

        composable<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav> { backStackEntry ->
            val splitReceiptForAllViewModel: SplitReceiptForAllViewModel = koinViewModel()
            val data =
                backStackEntry.toRoute<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav>()
            LaunchedEffect(key1 = Unit) {
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.SetAssignedConsumerNamesList(
                        assignedConsumerNamesList = data.assignedConsumerNamesList
                    )
                )
                splitReceiptForAllViewModel.setEvent(
                    SplitReceiptForAllEvents.RetrieveReceiptData(
                        data.receiptId
                    )
                )
                splitReceiptForAllViewModel.setEvent(SplitReceiptForAllEvents.ActivateOrderReportCreator)
            }
            SplitReceiptForAllScreen(
                receiptViewModel = receiptViewModel,
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

        composable<ReceiptNavHostDestinations.FolderReceiptsScreenNav> { backStackEntry ->
            val folderReceiptsViewModel: FolderReceiptsViewModel = koinViewModel()
            val data = backStackEntry.toRoute<ReceiptNavHostDestinations.FolderReceiptsScreenNav>()
            LaunchedEffect(key1 = data.folderId) {
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.RetrieveAllReceiptsForSpecificFolder(data.folderId)
                )
                folderReceiptsViewModel.setEvent(
                    FolderReceiptsEvent.RetrieveFolderData(data.folderId)
                )
            }
            FolderReceiptScreen(
                receiptViewModel = receiptViewModel,
                folderReceiptsViewModel = folderReceiptsViewModel,
                folderId = data.folderId,
                folderName = data.folderName,
            )
        }

        composable<ReceiptNavHostDestinations.ShowReportsScreenNav> { backStackEntry ->
            val data =
                backStackEntry.toRoute<ReceiptNavHostDestinations.ShowReportsScreenNav>()
            ShowReportsScreen(
                receiptViewModel = receiptViewModel,
                receiptReports = ReceiptReports(
                    shortReport = data.shortReport,
                    longReport = data.longReport,
                    basicReport = data.basicReport,
                ),
            )
        }

        composable<ReceiptNavHostDestinations.SplitReceiptForOneScreenNav> { backStackEntry ->
            val splitReceiptForOneViewModel: SplitReceiptForOneViewModel = koinViewModel()
            val data =
                backStackEntry.toRoute<ReceiptNavHostDestinations.SplitReceiptForOneScreenNav>()

            splitReceiptForOneViewModel.setEvent(
                SplitReceiptForOneEvent.RetrieveReceiptData(
                    data.receiptId
                )
            )
            splitReceiptForOneViewModel.setEvent(SplitReceiptForOneEvent.ActivateOrderReportCreator)

            SplitReceiptForOneScreen(
                receiptViewModel = receiptViewModel,
                splitReceiptForOneViewModel = splitReceiptForOneViewModel,
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
        is ReceiptIntent.GoToSplitReceiptForAllScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.SplitReceiptForAllScreenNav(
                    receiptId = intent.receiptId,
                    assignedConsumerNamesList = intent.assignedConsumerNamesList,
                )
            ) {
                popUpTo<ReceiptNavHostDestinations.EditReceiptScreenNav> {
                    inclusive = true
                }
            }
        }

        is ReceiptIntent.GoToSplitReceiptForOneScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.SplitReceiptForOneScreenNav(
                    receiptId = intent.receiptId,
                )
            )
        }

        is ReceiptIntent.GoToCreateReceiptScreen -> {
            navHostController.navigate(ReceiptNavHostDestinations.CreateReceiptScreenNav(folderId = null))
        }

        is ReceiptIntent.GoToEditReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.EditReceiptScreenNav(receiptId = intent.receiptId)
            ) {
                popUpTo<ReceiptNavHostDestinations.SplitReceiptForAllScreenNav> {
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

        is ReceiptIntent.GoToCreateReceiptScreenFromFolder -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.CreateReceiptScreenNav(folderId = intent.folderId)
            )
        }

        is ReceiptIntent.GoToFolderReceiptsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.FolderReceiptsScreenNav(
                    folderId = intent.folderId,
                    folderName = intent.folderName,
                )
            )
        }

        is ReceiptIntent.GoToShowReportsScreen -> {
            navHostController.navigate(
                ReceiptNavHostDestinations.ShowReportsScreenNav(
                    shortReport = intent.receiptReports.shortReport,
                    longReport = intent.receiptReports.longReport,
                    basicReport = intent.receiptReports.basicReport,
                )
            )
        }
    }
}

sealed interface ReceiptNavHostDestinations {
    @Serializable
    class CreateReceiptScreenNav(val folderId: Long?) : ReceiptNavHostDestinations

    @Serializable
    object AllReceiptsScreenNav : ReceiptNavHostDestinations

    @Serializable
    class SplitReceiptForAllScreenNav(
        val receiptId: Long,
        val assignedConsumerNamesList: List<String>,
    ) : ReceiptNavHostDestinations

    @Serializable
    class SplitReceiptForOneScreenNav(val receiptId: Long) : ReceiptNavHostDestinations

    @Serializable
    class EditReceiptScreenNav(val receiptId: Long) : ReceiptNavHostDestinations

    @Serializable
    class FolderReceiptsScreenNav(val folderId: Long, val folderName: String) :
        ReceiptNavHostDestinations

    @Serializable
    class ShowReportsScreenNav(
        val shortReport: String,
        val longReport: String,
        val basicReport: String,
    ) : ReceiptNavHostDestinations
}