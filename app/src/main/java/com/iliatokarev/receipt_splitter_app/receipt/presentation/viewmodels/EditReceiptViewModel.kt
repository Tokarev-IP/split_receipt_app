package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicSimpleViewModel
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.EditReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditReceiptViewModel(
    private val editReceiptUseCase: EditReceiptUseCaseInterface,
) : BasicSimpleViewModel<EditReceiptEvent>() {

    private val receiptData = MutableStateFlow<ReceiptData?>(null)
    private val receiptDataState = receiptData.asStateFlow()

    private val orderDataList = MutableStateFlow<List<OrderData>>(emptyList())
    private val orderDataListState = orderDataList.asStateFlow()

    private val isOrderCountAtLimit = MutableStateFlow(false)
    private val isOrderCountAtLimitState = isOrderCountAtLimit.asStateFlow()

    private fun setReceiptData(newReceiptData: ReceiptData?) {
        receiptData.value = newReceiptData
    }

    private fun setOrderDataList(newOrderDataList: List<OrderData>) {
        orderDataList.value = newOrderDataList
    }

    fun setIsOrderCountAtLimit(newState: Boolean) {
        isOrderCountAtLimit.value = newState
    }

    fun getReceiptData() = receiptDataState
    fun getOrderDataList() = orderDataListState
    fun getIsOrderCountAtLimit() = isOrderCountAtLimitState

    override fun setEvent(newEvent: EditReceiptEvent) {
        when (newEvent) {
            is EditReceiptEvent.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
                retrieveOrderDataList(receiptId = newEvent.receiptId)
                monitorAmountOfOrders()
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

    private fun monitorAmountOfOrders(){
        viewModelScope.launch(Dispatchers.Default) {
            orderDataList.collect { list ->
                if (list.size > DataConstantsReceipt.MAXIMUM_AMOUNT_OF_DISHES)
                    setIsOrderCountAtLimit(true)
                else
                    setIsOrderCountAtLimit(false)
            }
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