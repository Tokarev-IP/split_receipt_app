package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicSimpleViewModel
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitReceiptForOneViewModel(
    private val orderReportCreator: OrderReportCreatorInterface,
    private val splitReceiptUseCase: SplitReceiptUseCaseInterface,
    private val orderDataService: OrderDataServiceInterface,
) : BasicSimpleViewModel<SplitReceiptForOneEvent>() {

    private val splitReceiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private val splitOrderDataListFlow = MutableStateFlow<List<OrderData>>(emptyList())
    private val splitOrderDataListState = splitOrderDataListFlow.asStateFlow()

    private val orderReportTextFlow = MutableSharedFlow<String?>()
    private val orderReportTextState = orderReportTextFlow.asSharedFlow()

    private fun setSplitReceiptData(data: ReceiptData?) {
        splitReceiptDataFlow.value = data
    }

    private fun setOrderDataList(list: List<OrderData>) {
        splitOrderDataListFlow.value = list
    }


    private fun setOrderReportText(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            orderReportTextFlow.emit(text)
        }
    }

    fun getSplitReceiptData() = splitReceiptDataState
    fun getOrderDataList() = splitOrderDataListState
    fun getOrderReportText() = orderReportTextState

    override fun setEvent(newEvent: SplitReceiptForOneEvent) {
        when (newEvent) {
            is SplitReceiptForOneEvent.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptForOneEvent.AddOneQuantityToSpecificOrder -> {
                addOneQuantityToSpecificOrder(
                    orderDataList = splitOrderDataListState.value,
                    orderId = newEvent.orderId,
                )
            }

            is SplitReceiptForOneEvent.RemoveOneQuantityToSpecificOrder -> {
                removeQuantityToSpecificOrder(
                    orderDataList = splitOrderDataListState.value,
                    orderId = newEvent.orderId,
                )
            }

            is SplitReceiptForOneEvent.ActivateOrderReportCreator -> {
                monitorOrderData()
            }

            is SplitReceiptForOneEvent.ClearOrderReport -> {
                clearQuantityToSpecificOrder(
                    orderDataList = splitOrderDataListState.value,
                )
            }
        }
    }

    private fun createOrderReport(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>
    ) {
        viewModelScope.launch {
            val response = orderReportCreator.buildOrderReportForOne(
                receiptData = receiptData,
                orderDataList = orderDataList,
            )
            setOrderReportText(response)
        }
    }

    private fun retrieveReceiptData(receiptId: Long) {
        viewModelScope.launch {
            launch {
                splitReceiptUseCase.retrieveReceiptData(receiptId = receiptId).run {
                    setSplitReceiptData(this)
                }
            }
            launch {
                splitReceiptUseCase.retrieveOrderDataList(receiptId = receiptId).run {
                    setOrderDataList(this)
                }
            }
        }
    }

    private fun addOneQuantityToSpecificOrder(
        orderId: Long,
        orderDataList: List<OrderData>,
    ) {
        viewModelScope.launch {
            val newOrderDataList = orderDataService.addQuantityToSpecificOrderData(
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
            val newOrderDataList = orderDataService.subtractQuantityToSpecificOrderData(
                orderDataList = orderDataList,
                orderId = orderId
            )
            setOrderDataList(newOrderDataList)
        }
    }

    private fun monitorOrderData() {
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun clearQuantityToSpecificOrder(
        orderDataList: List<OrderData>,
    ) {
        viewModelScope.launch {
            val newOrderDataList = orderDataService.clearAllQuantity(
                orderDataList = orderDataList,
            )
            setOrderDataList(newOrderDataList)
        }
    }
}

sealed interface SplitReceiptForOneEvent : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptForOneEvent
    class AddOneQuantityToSpecificOrder(val orderId: Long) : SplitReceiptForOneEvent
    class RemoveOneQuantityToSpecificOrder(val orderId: Long) : SplitReceiptForOneEvent
    object ActivateOrderReportCreator : SplitReceiptForOneEvent
    object ClearOrderReport : SplitReceiptForOneEvent
}