package com.iliatokarev.receipt_splitter_app.receipt.presentation

import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoBackNavigation
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToAllReceiptsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToCreateReceiptScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToCreateReceiptScreenFromFolder
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToEditReceiptsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToFolderReceiptsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToSettings
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToShowReportsScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToSplitReceiptForAllScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.GoToSplitReceiptForOneScreen
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.NewReceiptIsCreated
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptIntent.UserIsEmpty
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessageIntent.UiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels.ReceiptReports

class ReceiptViewModel() : BasicViewModel<
        ReceiptUiState,
        ReceiptIntent,
        ReceiptEvent,
        ReceiptUiMessageIntent>(
    initialUiState = ReceiptUiState.Show,
) {
    override fun setEvent(newEvent: ReceiptEvent) {
        when (newEvent) {
            is ReceiptEvent.OpenSplitReceiptForAllScreen -> {
                setIntent(
                    GoToSplitReceiptForAllScreen(
                        receiptId = newEvent.receiptId,
                        assignedConsumerNamesList = newEvent.assignedConsumerNamesList,
                    )
                )
            }

            is ReceiptEvent.OpenCreateReceiptScreen -> {
                setIntent(GoToCreateReceiptScreen)
            }

            is ReceiptEvent.OpenEditReceiptsScreen -> {
                setIntent(GoToEditReceiptsScreen(newEvent.receiptId))
            }

            is ReceiptEvent.OpenAllReceiptsScreen -> {
                setIntent(GoToAllReceiptsScreen)
            }

            is ReceiptEvent.GoBack -> {
                setIntent(GoBackNavigation)
            }

            is ReceiptEvent.OpenSettings -> {
                setIntent(GoToSettings)
            }

            is ReceiptEvent.NewReceiptIsCreated -> {
                setIntent(NewReceiptIsCreated(newEvent.receiptId))
            }

            is ReceiptEvent.SignOut -> {
                setIntent(UserIsEmpty)
            }

            is ReceiptEvent.SetUiMessage -> {
                setUiMessageIntent(UiMessage(newEvent.message))
            }

            is ReceiptEvent.OpenCreateReceiptScreenFromFolder -> {
                setIntent(GoToCreateReceiptScreenFromFolder(newEvent.folderId))
            }

            is ReceiptEvent.OpenFolderReceiptsScreen -> {
                setIntent(GoToFolderReceiptsScreen(newEvent.folderId, newEvent.folderName))
            }

            is ReceiptEvent.OpenSplitReceiptForOneScreen -> {
                setIntent(GoToSplitReceiptForOneScreen(newEvent.receiptId))
            }

            is ReceiptEvent.OpenShowReportsScreen -> {
                setIntent(GoToShowReportsScreen(newEvent.receiptReports))
            }
        }
    }
}

interface ReceiptUiState : BasicUiState {
    object Loading : ReceiptUiState
    object Show : ReceiptUiState
}

sealed interface ReceiptEvent : BasicEvent {
    class OpenSplitReceiptForAllScreen(
        val receiptId: Long,
        val assignedConsumerNamesList: List<String> = emptyList<String>(),
    ) : ReceiptEvent

    class OpenSplitReceiptForOneScreen(val receiptId: Long) : ReceiptEvent
    object OpenCreateReceiptScreen : ReceiptEvent
    class OpenEditReceiptsScreen(val receiptId: Long) : ReceiptEvent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptEvent
    object OpenAllReceiptsScreen : ReceiptEvent
    object GoBack : ReceiptEvent
    object OpenSettings : ReceiptEvent
    object SignOut : ReceiptEvent
    class SetUiMessage(val message: String) : ReceiptEvent
    class OpenCreateReceiptScreenFromFolder(val folderId: Long?) : ReceiptEvent
    class OpenFolderReceiptsScreen(val folderId: Long, val folderName: String) : ReceiptEvent
    class OpenShowReportsScreen(val receiptReports: ReceiptReports) : ReceiptEvent
}

interface ReceiptIntent : BasicIntent {
    class GoToSplitReceiptForAllScreen(
        val receiptId: Long,
        val assignedConsumerNamesList: List<String>,
    ) : ReceiptIntent

    class GoToSplitReceiptForOneScreen(val receiptId: Long) : ReceiptIntent
    object GoToCreateReceiptScreen : ReceiptIntent
    class GoToEditReceiptsScreen(val receiptId: Long) : ReceiptIntent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptIntent
    object GoToAllReceiptsScreen : ReceiptIntent
    object GoBackNavigation : ReceiptIntent
    object GoToSettings : ReceiptIntent
    object UserIsEmpty : ReceiptIntent
    class GoToCreateReceiptScreenFromFolder(val folderId: Long?) : ReceiptIntent
    class GoToFolderReceiptsScreen(val folderId: Long, val folderName: String) : ReceiptIntent
    class GoToShowReportsScreen(val receiptReports: ReceiptReports) : ReceiptIntent
}

interface ReceiptUiMessageIntent : BasicUiMessageIntent {
    class UiMessage(val message: String) : ReceiptUiMessageIntent
}

enum class ReceiptUiMessage(val msg: String) {
    INTERNAL_ERROR("Internal error"),
}