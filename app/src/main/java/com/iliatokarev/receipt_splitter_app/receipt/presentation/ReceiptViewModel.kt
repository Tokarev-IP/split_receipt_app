package com.iliatokarev.receipt_splitter_app.receipt.presentation

import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel

class ReceiptViewModel() : BasicViewModel<
        ReceiptUiState,
        ReceiptIntent,
        ReceiptEvent,
        ReceiptUiMessageIntent>(
    initialUiState = ReceiptUiState.Show,
) {
    override fun setEvent(newEvent: ReceiptEvent)  {
        when (newEvent) {
            is ReceiptEvent.OpenSplitReceiptScreen -> {
                setIntent(ReceiptIntent.GoToSplitReceiptScreen(newEvent.receiptId))
            }

            is ReceiptEvent.OpenCreateReceiptScreen -> {
                setIntent(ReceiptIntent.GoToCreateReceiptScreen)
            }

            is ReceiptEvent.OpenEditReceiptsScreen -> {
                setIntent(ReceiptIntent.GoToEditReceiptsScreen(newEvent.receiptId))
            }

            is ReceiptEvent.OpenAllReceiptsScreen -> {
                setIntent(ReceiptIntent.GoToAllReceiptsScreen)
            }

            is ReceiptEvent.GoBack -> {
                setIntent(ReceiptIntent.GoBackNavigation)
            }

            is ReceiptEvent.OpenSettings -> {
                setIntent(ReceiptIntent.GoToSettings)
            }

            is ReceiptEvent.NewReceiptIsCreated -> {
                setIntent(ReceiptIntent.NewReceiptIsCreated(newEvent.receiptId))
            }

            is ReceiptEvent.SignOut -> {
                setIntent(ReceiptIntent.UserIsEmpty)
            }
        }
    }
}

interface ReceiptUiState : BasicUiState {
    object Loading : ReceiptUiState
    object Show : ReceiptUiState
}

sealed interface ReceiptEvent : BasicEvent {
    class OpenSplitReceiptScreen(val receiptId: Long) : ReceiptEvent
    object OpenCreateReceiptScreen : ReceiptEvent
    class OpenEditReceiptsScreen(val receiptId: Long) : ReceiptEvent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptEvent
    object OpenAllReceiptsScreen : ReceiptEvent
    object GoBack : ReceiptEvent
    object OpenSettings : ReceiptEvent
    object SignOut : ReceiptEvent
}

interface ReceiptIntent : BasicIntent {
    class GoToSplitReceiptScreen(val receiptId: Long) : ReceiptIntent
    object GoToCreateReceiptScreen : ReceiptIntent
    class GoToEditReceiptsScreen(val receiptId: Long) : ReceiptIntent
    class NewReceiptIsCreated(val receiptId: Long) : ReceiptIntent
    object GoToAllReceiptsScreen : ReceiptIntent
    object GoBackNavigation : ReceiptIntent
    object GoToSettings : ReceiptIntent
    object UserIsEmpty : ReceiptIntent
}

interface ReceiptUiMessageIntent : BasicUiMessageIntent

enum class ReceiptUiMessage(val msg: String) {
    INTERNAL_ERROR("Internal error"),
}