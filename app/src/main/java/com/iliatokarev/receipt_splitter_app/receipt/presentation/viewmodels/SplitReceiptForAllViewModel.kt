package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicSimpleViewModel
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataCheckService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataCheck
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitReceiptForAllViewModel(
    private val orderReportCreator: OrderReportCreatorInterface,
    private val splitReceiptUseCase: SplitReceiptUseCase,
    private val orderDataCheckService: OrderDataCheckService,
) : BasicSimpleViewModel<SplitReceiptForAllEvents>() {

    private val splitReceiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private val splitOrderDataCheckListFlow = MutableStateFlow<List<OrderDataCheck>>(emptyList())
    private val splitOrderDataCheckListState = splitOrderDataCheckListFlow.asStateFlow()

    private val orderReportTextFlow = MutableSharedFlow<String?>()
    private val orderReportTextState = orderReportTextFlow.asSharedFlow()

    private val consumerNameListFlow = MutableStateFlow<List<String>>(emptyList())
    private val consumerNameListState = consumerNameListFlow.asStateFlow()

    private val checkStateExistenceFlow = MutableStateFlow(false)
    private val checkStateExistenceState = checkStateExistenceFlow.asStateFlow()

    fun setSplitReceiptData(data: ReceiptData?) {
        splitReceiptDataFlow.value = data
    }


    fun setOrderDataCheckList(list: List<OrderDataCheck>) {
        splitOrderDataCheckListFlow.value = list
    }

    fun setOrderReportText(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            orderReportTextFlow.emit(text)
        }
    }

    fun setConsumerNameList(list: List<String>) {
        consumerNameListFlow.value = list
    }

    fun setCheckStateExistence(state: Boolean) {
        checkStateExistenceFlow.value = state
    }

    fun getSplitReceiptData() = splitReceiptDataState
    fun getOrderDataCheckList() = splitOrderDataCheckListState
    fun getOrderReportText() = orderReportTextState
    fun getConsumerNameList() = consumerNameListState
    fun getIsCheckStateExisted() = checkStateExistenceState

    override fun setEvent(newEvent: SplitReceiptForAllEvents) {
        when (newEvent) {
            is SplitReceiptForAllEvents.ActivateOrderReportCreator -> {
                monitorOrderDataCheck()
            }

            is SplitReceiptForAllEvents.ClearConsumerName -> {
                clearConsumerName(
                    orderDataCheckList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                )
            }

            is SplitReceiptForAllEvents.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptForAllEvents.SetCheckState -> {
                setCheckState(
                    orderDataCheckList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    state = newEvent.state,
                )
            }

            is SplitReceiptForAllEvents.SetConsumerName -> {
                setConsumerName(
                    orderDataCheckList = splitOrderDataCheckListState.value,
                    name = newEvent.name,
                )
            }

            is SplitReceiptForAllEvents.ClearOrderReport -> {
                clearOrderReport(
                    orderDataCheckList = splitOrderDataCheckListState.value,
                )
            }
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
                    val orderDataCheckList =
                        orderDataCheckService.convertOrderDataListToOrderDataCheckList(
                            orderDataList = this
                        )
                    setOrderDataCheckList(orderDataCheckList)
                }
            }
        }
    }

    private fun monitorOrderDataCheck() {
        viewModelScope.launch {
            splitOrderDataCheckListFlow.collect { orderDataCheckList ->
                splitReceiptDataFlow.value?.let { receipt ->
                    createOrderReport(
                        receiptData = receipt,
                        orderDataCheckList = orderDataCheckList,
                    )
                }
                setAllConsumerNames(orderDataCheckList = orderDataCheckList)
                setCheckStateExistence(orderDataCheckList = orderDataCheckList)
            }
        }
    }

    private fun clearConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataCheckService.clearConsumerName(
                orderDataCheckList = orderDataCheckList,
                position = position,
            )
            setOrderDataCheckList(newOrderDataCheckList)
        }
    }

    private fun setConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        name: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataCheckService.setConsumerName(
                orderDataCheckList = orderDataCheckList,
                name = name,
            )
            setOrderDataCheckList(newOrderDataCheckList)
        }
    }

    private fun setCheckState(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int,
        state: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataCheckService.setCheckState(
                orderDataCheckList = orderDataCheckList,
                position = position,
                state = state,
            )
            setOrderDataCheckList(newOrderDataCheckList)
        }
    }

    private fun createOrderReport(
        receiptData: ReceiptData,
        orderDataCheckList: List<OrderDataCheck>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentConsumerList = orderDataCheckService.getAllConsumerNames(
                orderDataCheckList = orderDataCheckList,
            )
            val response = orderReportCreator.buildOrderReportForAll(
                receiptData = receiptData,
                orderDataCheckList = orderDataCheckList,
                consumerNameList = currentConsumerList,
            )
            setOrderReportText(response)
        }
    }

    private fun clearOrderReport(
        orderDataCheckList: List<OrderDataCheck>,
    ){
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataCheckService.clearAllCheckState(
                orderDataCheckList = orderDataCheckList,
            )
            setOrderDataCheckList(newOrderDataCheckList)
        }
    }

    private fun setAllConsumerNames(
        orderDataCheckList: List<OrderDataCheck>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newConsumerNameList = orderDataCheckService.getAllConsumerNames(
                orderDataCheckList = orderDataCheckList,
            )
            setConsumerNameList(newConsumerNameList)
        }
    }

    private fun setCheckStateExistence(
        orderDataCheckList: List<OrderDataCheck>,
    ){
        viewModelScope.launch(Dispatchers.Default) {
            val newIsCheckStateExisted = orderDataCheckService.hasExistingCheckState(
                orderDataCheckList = orderDataCheckList,
            )
            setCheckStateExistence(newIsCheckStateExisted)
        }
    }
}

sealed interface SplitReceiptForAllEvents : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptForAllEvents
    object ActivateOrderReportCreator : SplitReceiptForAllEvents
    class SetCheckState(val position: Int, val state: Boolean) : SplitReceiptForAllEvents
    class SetConsumerName(val name: String) : SplitReceiptForAllEvents
    class ClearConsumerName(val position: Int) : SplitReceiptForAllEvents
    object ClearOrderReport : SplitReceiptForAllEvents
}