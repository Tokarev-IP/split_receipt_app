package com.example.receipt_splitter.receipt.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicSimpleViewModel
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditReceiptViewModel(
    private val editReceiptUseCase: EditReceiptUseCaseInterface,
) : BasicSimpleViewModel<
        EditReceiptUiState,
        EditReceiptEvent,
        EditReceiptUiMessageIntent>(initialUiState = EditReceiptUiState.Show) {

    private val receiptData = MutableStateFlow<ReceiptData?>(null)
    private val receiptDataState = receiptData.asStateFlow()

    private val orderDataList = MutableStateFlow<List<OrderData>>(emptyList())
    private val orderDataListState = orderDataList.asStateFlow()

    private fun setReceiptData(newReceiptData: ReceiptData?) {
        receiptData.value = newReceiptData
    }
    private fun setOrderDataList(newOrderDataList: List<OrderData>) {
        orderDataList.value = newOrderDataList
    }

    fun getReceiptData() = receiptDataState
    fun getOrderDataList() = orderDataListState

    override fun setEvent(newEvent: EditReceiptEvent) {
        when (newEvent) {
            is EditReceiptEvent.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
                retrieveOrderDataList(receiptId = newEvent.receiptId)
            }

            is EditReceiptEvent.DeleteOrder -> {
                deleteOrderData(orderId = newEvent.orderId)
            }

            is EditReceiptEvent.EditOrder -> {
                editOrderData(orderData = newEvent.order)
            }

            is EditReceiptEvent.EditReceipt -> {
                editReceiptData(receiptData = newEvent.receipt)
            }

            is EditReceiptEvent.AddNewOrder -> {
                addNewOrderData(orderData = newEvent.order)
            }
        }
    }

    private fun retrieveReceiptData(receiptId: Long) {
        viewModelScope.launch {
            editReceiptUseCase.getReceiptDataFlow(receiptId = receiptId).collect { data ->
                setReceiptData(newReceiptData = data)
            }
        }
    }

    private fun retrieveOrderDataList(receiptId: Long) {
        viewModelScope.launch {
            editReceiptUseCase.getOrderDataListFlow(receiptId = receiptId).collect { list ->
                Log.d("TOKAR", "order list: $list")
                setOrderDataList(newOrderDataList = list)
            }
        }
    }

    private fun deleteOrderData(orderId: Long) {
        viewModelScope.launch {
            editReceiptUseCase.deleteOrderDataById(id = orderId)
        }
    }

    private fun editOrderData(orderData: OrderData) {
        viewModelScope.launch {
            editReceiptUseCase.upsertOrderData(orderData = orderData)
        }
    }

    private fun editReceiptData(receiptData: ReceiptData) {
        viewModelScope.launch {
            editReceiptUseCase.upsertReceiptData(receiptData = receiptData)
        }
    }

    private fun addNewOrderData(orderData: OrderData) {
        viewModelScope.launch {
            editReceiptUseCase.insertNewOrderData(orderData = orderData)
        }
    }

}

interface EditReceiptUiState : BasicUiState {
    object Show : EditReceiptUiState
    object Loading : EditReceiptUiState
}

sealed interface EditReceiptEvent : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : EditReceiptEvent
    class DeleteOrder(val orderId: Long) : EditReceiptEvent
    class EditOrder(val order: OrderData) : EditReceiptEvent
    class EditReceipt(val receipt: ReceiptData) : EditReceiptEvent
    class AddNewOrder(val order: OrderData) : EditReceiptEvent
}

interface EditReceiptUiMessageIntent : BasicUiMessageIntent