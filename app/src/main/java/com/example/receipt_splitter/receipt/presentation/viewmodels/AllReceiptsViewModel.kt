package com.example.receipt_splitter.receipt.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicSimpleViewModel
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
) : BasicSimpleViewModel<
        AllReceiptsUiState,
        AllReceiptsEvent,
        AllReceiptsUiMessageIntent>(initialUiState = AllReceiptsUiState.Loading) {

    private val allReceiptsList = MutableStateFlow<List<ReceiptData>?>(null)
    private val allReceiptsListState = allReceiptsList.asStateFlow()

    private fun setAllReceiptsList(newList: List<ReceiptData>) {
        allReceiptsList.value = newList
    }

    fun getAllReceiptsList() = allReceiptsListState

    override fun setEvent(newEvent: AllReceiptsEvent) {
        when (newEvent) {
            is AllReceiptsEvent.RetrieveAllReceipts -> {
                if (allReceiptsListState.value == null)
                    retrieveAllReceipts()
            }

            is AllReceiptsEvent.DeleteSpecificReceipt -> {
                deleteReceiptData(receiptId = newEvent.receiptId)
            }
        }
    }

    private fun retrieveAllReceipts() {
        viewModelScope.launch {
            allReceiptsUseCase.getAllReceiptsFlow().collect { data: List<ReceiptData> ->
                Log.d("TOKAR", "all receipts: $data")
                setAllReceiptsList(data.reversed())
            }
        }
    }

    private fun deleteReceiptData(receiptId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.deleteReceiptData(receiptId = receiptId)
        }
    }

}

interface AllReceiptsUiState : BasicUiState {
    object Loading : AllReceiptsUiState
    object Show : AllReceiptsUiState
}

sealed interface AllReceiptsEvent : BasicEvent {
    object RetrieveAllReceipts : AllReceiptsEvent
    data class DeleteSpecificReceipt(val receiptId: Long) : AllReceiptsEvent
}

interface AllReceiptsUiMessageIntent : BasicUiMessageIntent