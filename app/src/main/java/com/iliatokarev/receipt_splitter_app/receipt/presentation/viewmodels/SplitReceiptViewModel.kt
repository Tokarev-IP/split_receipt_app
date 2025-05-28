package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicSimpleViewModel
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitter
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitReceiptViewModel(
    private val orderReportCreator: OrderReportCreatorInterface,
    private val splitReceiptUseCase: SplitReceiptUseCase,
    private val orderDataSplitter: OrderDataSplitter,
) : BasicSimpleViewModel<SplitReceiptEvent>() {

    private val splitReceiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private val splitOrderDataListFlow = MutableStateFlow<List<OrderData>>(emptyList())
    private val splitOrderDataListState = splitOrderDataListFlow.asStateFlow()

    private val orderReportTextFlow = MutableSharedFlow<String?>()
    private val orderReportTextState = orderReportTextFlow.asSharedFlow()

    fun setSplitReceiptData(data: ReceiptData?) {
        splitReceiptDataFlow.value = data
    }

    fun setOrderDataList(list: List<OrderData>) {
        splitOrderDataListFlow.value = list
    }

    suspend fun setOrderReportText(text: String?) {
        orderReportTextFlow.emit(text)
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
                    viewModelScope.launch {
                        setOrderReportText(null)
                    }
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

            is SplitReceiptEvent.AddAdditionalSum -> {
                splitReceiptDataFlow.value?.let { receiptData ->
                    addAdditionalSum(
                        receiptData = receiptData,
                        additionalSum = newEvent.pair,
                        orderList = splitOrderDataListState.value,
                    )
                }
            }

            is SplitReceiptEvent.RemoveAdditionalSum -> {
                splitReceiptDataFlow.value?.let { receiptData ->
                    removeSpecificAdditionalSum(
                        receiptData = receiptData,
                        additionalSum = newEvent.pair,
                        orderList = splitOrderDataListState.value,
                    )
                }
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
            launch {
                splitReceiptUseCase.retrieveReceiptData(receiptId = receiptId).run {
                    if (this != null)
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

    private fun monitorOrderData() {
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

    private fun addAdditionalSum(
        receiptData: ReceiptData,
        additionalSum: Pair<String, Float>,
        orderList: List<OrderData>,
    ) {
        val newList = receiptData.additionalSumList.toMutableList().apply { add(additionalSum) }
        val newReceipt = receiptData.copy(
            additionalSumList = newList
        )
        setSplitReceiptData(newReceipt)
        createOrderReport(
            receiptData = newReceipt,
            orderDataList = orderList,
        )
    }

    private fun removeSpecificAdditionalSum(
        receiptData: ReceiptData,
        additionalSum: Pair<String, Float>,
        orderList: List<OrderData>,
    ) {
        val newList = receiptData.additionalSumList.toMutableList().apply { remove(additionalSum) }
        val newReceipt = receiptData.copy(
            additionalSumList = newList
        )
        setSplitReceiptData(newReceipt)
        createOrderReport(
            receiptData = newReceipt,
            orderDataList = orderList,
        )
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
    class RemoveAdditionalSum(val pair: Pair<String, Float>) : SplitReceiptEvent
    class AddAdditionalSum(val pair: Pair<String, Float>) : SplitReceiptEvent
}