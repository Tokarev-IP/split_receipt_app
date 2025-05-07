package com.example.receipt_splitter.receipt.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicSimpleViewModel
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.receipt.domain.OrderDataSplitter
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorInterface
import com.example.receipt_splitter.receipt.domain.usecases.SplitReceiptUseCase
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitReceiptViewModel(
    private val orderReportCreator: OrderReportCreatorInterface,
    private val splitReceiptUseCase: SplitReceiptUseCase,
    private val orderDataSplitter: OrderDataSplitter,
) : BasicSimpleViewModel<
        SplitReceiptUiState,
        SplitReceiptEvent,
        SplitReceiptUiMessageIntent>(initialUiState = SplitReceiptUiState.Show) {

    private val splitReceiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private val splitOrderDataListFlow = MutableStateFlow<List<OrderData>>(emptyList())
    private val splitOrderDataListState = splitOrderDataListFlow.asStateFlow()

    private val orderReportTextFlow = MutableStateFlow<String?>(null)
    private val orderReportTextState = orderReportTextFlow.asStateFlow()

    fun setSplitReceiptData(data: ReceiptData) {
        splitReceiptDataFlow.value = data
    }

    fun setOrderDataList(list: List<OrderData>) {
        splitOrderDataListFlow.value = list
    }

    fun setOrderReportText(text: String?) {
        orderReportTextFlow.value = text
    }

    fun getSplitReceiptData() = splitReceiptDataState
    fun getOrderDataList() = splitOrderDataListState
    fun getOrderReportText() = orderReportTextState

    override fun setEvent(newEvent: SplitReceiptEvent) {
        when (newEvent) {
            is SplitReceiptEvent.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptEvent.GenerateReportText -> {
                splitReceiptDataFlow.value?.let { receiptData ->
                    createOrderReport(
                        receiptData = receiptData,
                        orderDataList = splitOrderDataListFlow.value,
                    )
                } ?: {
                    //todo
                }
            }

            is SplitReceiptEvent.AddOneQuantityToSpecificOrder -> {
                addOneQuantityToSpecificOrder(
                    orderDataList = splitOrderDataListState.value,
                    orderId = newEvent.orderId,
                )
            }

            is SplitReceiptEvent.RemoveOneQuantityToSpecificOrder -> {
                removeQuantityToSpecificOrder(
                    orderDataList = splitOrderDataListState.value,
                    orderId = newEvent.orderId,
                )
            }

            is SplitReceiptEvent.ActivateOrderReportCreator -> {
                monitorOrderData()
            }
        }
    }

    private fun createOrderReport(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>
    ) {
        viewModelScope.launch {
            val response = orderReportCreator.buildOrderReport(
                receiptData = receiptData,
                orderDataList = orderDataList,
            )
            setOrderReportText(response)
        }
    }

    private fun retrieveReceiptData(receiptId: Long) {
        viewModelScope.launch {
            splitReceiptUseCase.retrieveReceiptData(receiptId = receiptId).run {
                if (this != null)
                    setSplitReceiptData(this)
                Log.d("TOKAR", "receipt data: $this")
            }
            splitReceiptUseCase.retrieveOrderDataList(receiptId = receiptId).run {
                setOrderDataList(this)
                Log.d("TOKAR", "order data list: $this")
            }
        }
    }

    private fun addOneQuantityToSpecificOrder(
        orderId: Long,
        orderDataList: List<OrderData>,
    ) {
        viewModelScope.launch {
            val newOrderDataList = orderDataSplitter.addQuantityToSpecificOrderData(
                orderDataList = orderDataList,
                orderId = orderId,
            )
            setOrderDataList(newOrderDataList)
        }
    }

    private fun removeQuantityToSpecificOrder(
        orderId: Long,
        orderDataList: List<OrderData>,
    ) {
        viewModelScope.launch {
            val newOrderDataList = orderDataSplitter.subtractQuantityToSpecificOrderData(
                orderDataList = orderDataList,
                orderId = orderId
            )
            setOrderDataList(newOrderDataList)
        }
    }

    private fun monitorOrderData(){
        viewModelScope.launch {
            splitOrderDataListFlow.collect { orders ->
                splitReceiptDataFlow.value?.let { receipt ->
                    createOrderReport(
                        receiptData = receipt,
                        orderDataList = orders
                    )
                }
            }
        }
    }
}

interface SplitReceiptUiState : BasicUiState {
    object Show : SplitReceiptUiState
    object Loading : SplitReceiptUiState
}

sealed interface SplitReceiptEvent : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptEvent
    object GenerateReportText : SplitReceiptEvent
    class AddOneQuantityToSpecificOrder(val orderId: Long) : SplitReceiptEvent
    class RemoveOneQuantityToSpecificOrder(val orderId: Long) : SplitReceiptEvent
    object ActivateOrderReportCreator : SplitReceiptEvent
}

interface SplitReceiptUiMessageIntent : BasicUiMessageIntent