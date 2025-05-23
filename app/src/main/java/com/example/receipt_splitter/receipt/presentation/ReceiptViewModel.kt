package com.example.receipt_splitter.receipt.presentation

import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.main.basic.BasicViewModel

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
    object OpenAllReceiptsScreen : ReceiptEvent
    object GoBack : ReceiptEvent
    object OpenSettings : ReceiptEvent
}

interface ReceiptIntent : BasicIntent {
    class GoToSplitReceiptScreen(val receiptId: Long) : ReceiptIntent
    object GoToCreateReceiptScreen : ReceiptIntent
    class GoToEditReceiptsScreen(val receiptId: Long) : ReceiptIntent
    object GoToAllReceiptsScreen : ReceiptIntent
    object GoBackNavigation : ReceiptIntent
    object GoToSettings : ReceiptIntent
}

interface ReceiptUiMessageIntent : BasicUiMessageIntent

enum class ReceiptUiMessage(val msg: String) {
    INTERNAL_ERROR("Internal error"),
}