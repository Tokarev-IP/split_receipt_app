package com.example.receipt_splitter.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicSimpleViewModel
import com.example.receipt_splitter.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
) : BasicSimpleViewModel<AllReceiptsEvent>() {

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

sealed interface AllReceiptsEvent : BasicEvent {
    object RetrieveAllReceipts : AllReceiptsEvent
    data class DeleteSpecificReceipt(val receiptId: Long) : AllReceiptsEvent
}