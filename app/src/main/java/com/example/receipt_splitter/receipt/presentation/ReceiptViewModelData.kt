package com.example.receipt_splitter.receipt.presentation

import android.net.Uri
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicNavigationEvent
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState

interface ReceiptUiState : BasicUiState {
    object Loading : ReceiptUiState
    object Show : ReceiptUiState
}

sealed interface ReceiptEvent : BasicEvent {
    class ConvertImagesToReceipt(val listOfImages: List<Uri>) : ReceiptEvent
    class AddQuantityToSplitOrderData(val orderId: Long) : ReceiptEvent
    class SubtractQuantityToSplitOrderData(val orderId: Long) : ReceiptEvent
    object AddNewReceipt : ReceiptEvent
    class ReceiptDeletion(val receiptId: Long) : ReceiptEvent
    object RetrieveAllReceipts : ReceiptEvent
    class OpenSplitReceiptScreen(val splitReceiptData: SplitReceiptData) : ReceiptEvent
    object SetShowState : ReceiptEvent
}

sealed interface ReceiptNavigationEvent : BasicNavigationEvent

interface ReceiptIntent : BasicIntent {
    object GoToSplitReceiptScreen : ReceiptIntent
    object GoToChoosePhotoScreen : ReceiptIntent
    object GoToShowReceiptsScreen : ReceiptIntent
}

interface ReceiptUiMessageIntent : BasicUiMessageIntent {
    object ImageIsInappropriate : ReceiptUiMessageIntent
    class ReceiptMessage(val msg: String) : ReceiptUiMessageIntent
}