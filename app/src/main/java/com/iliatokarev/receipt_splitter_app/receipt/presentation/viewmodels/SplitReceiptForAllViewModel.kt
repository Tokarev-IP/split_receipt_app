package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitService
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCase
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
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
    private val orderDataSplitService: OrderDataSplitService,
) : BasicViewModel<
        SplitReceiptForAllUiState,
        SplitReceiptForAllIntent,
        SplitReceiptForAllEvents,
        SplitReceiptForAllUiMessageIntent>(initialUiState = SplitReceiptForAllUiState.Show) {

    private var orderDataList: List<OrderData> = emptyList()
    private var assignedConsumerNamesList: List<String> = emptyList()

    private val splitReceiptDataFlow = MutableStateFlow<ReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private val splitOrderDataSplitListFlow = MutableStateFlow<List<OrderDataSplit>>(emptyList())
    private val splitOrderDataCheckListState = splitOrderDataSplitListFlow.asStateFlow()

    private val orderReportTextFlow = MutableSharedFlow<String?>()
    private val orderReportTextState = orderReportTextFlow.asSharedFlow()

    private val allConsumerNamesListFlow = MutableStateFlow<List<String>>(emptyList())
    private val allConsumerNamesListState = allConsumerNamesListFlow.asStateFlow()

    private val checkStateExistenceFlow = MutableStateFlow(false)
    private val checkStateExistenceState = checkStateExistenceFlow.asStateFlow()

    fun setSplitReceiptData(data: ReceiptData?) {
        splitReceiptDataFlow.value = data
    }


    fun setOrderDataSplitList(list: List<OrderDataSplit>) {
        splitOrderDataSplitListFlow.value = list
    }

    fun setOrderReportText(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            orderReportTextFlow.emit(text)
        }
    }

    fun setAllConsumerNamesList(list: List<String>) {
        allConsumerNamesListFlow.value = list
    }

    fun setCheckStateExistence(state: Boolean) {
        checkStateExistenceFlow.value = state
    }

    fun getSplitReceiptData() = splitReceiptDataState
    fun getOrderDataSplitList() = splitOrderDataCheckListState
    fun getOrderReportText() = orderReportTextState
    fun getAllConsumerNamesList() = allConsumerNamesListState
    fun getIsCheckStateExisted() = checkStateExistenceState

    override fun setEvent(newEvent: SplitReceiptForAllEvents) {
        when (newEvent) {
            is SplitReceiptForAllEvents.ActivateOrderReportCreator -> {
                monitorOrderDataCheck()
            }

            is SplitReceiptForAllEvents.ClearConsumerNameForOrder -> {
                clearSpecificConsumerNameForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    consumerName = newEvent.name,
                )
            }

            is SplitReceiptForAllEvents.RetrieveReceiptData -> {
                retrieveReceiptData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptForAllEvents.SetCheckState -> {
                setCheckState(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    state = newEvent.state,
                )
            }

            is SplitReceiptForAllEvents.SetConsumerNames -> {
                setConsumerNames(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    consumerNamesList = newEvent.consumerNamesList,
                )
            }

            is SplitReceiptForAllEvents.ClearOrderReport -> {
                clearOrderReport(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                )
            }

            is SplitReceiptForAllEvents.SaveOrderDataSplit -> {
                saveOrderDataSplitList(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    orderDataList = orderDataList,
                )
            }

            is SplitReceiptForAllEvents.ClearAllConsumerNames -> {
                clearAllConsumerNamesForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                )
            }

            is SplitReceiptForAllEvents.AddConsumerNameForSpecificOrder -> {
                addConsumerNameForSpecificOrder(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    name = newEvent.name,
                )
            }

            is SplitReceiptForAllEvents.AddNewNameForAllConsumerNames -> {
                addNewNameForAllConsumerNames(
                    name = newEvent.name,
                    allConsumerNamesList = allConsumerNamesListState.value,
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
                    orderDataList = this
                    val orderDataSplitList =
                        splitReceiptUseCase.convertOrderDataListToOrderDataSplitList(
                            orderDataList = this
                        )
                    setOrderDataSplitList(orderDataSplitList)
                }
            }
        }
    }

    private fun monitorOrderDataCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            splitOrderDataSplitListFlow.collect { orderDataSplitList ->
                splitReceiptDataFlow.value?.let { receipt ->
                    createOrderReport(
                        receiptData = receipt,
                        orderDataSplitList = orderDataSplitList,
                        initialConsumerNamesList = assignedConsumerNamesList,
                    )
                }
                setAllConsumerNames(
                    orderDataSplitList = orderDataSplitList,
                    initialConsumerNamesList = assignedConsumerNamesList,
                )
                setCheckStateExistence(orderDataSplitList = orderDataSplitList)
            }
        }
    }

    private fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList =
                orderDataSplitService.clearSpecificConsumerNameForSpecificOrder(
                    orderDataSplitList = orderDataSplitList,
                    position = position,
                    consumerName = consumerName,
                )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataSplitService.addNewConsumerNamesForCheckedOrders(
                orderDataSplitList = orderDataSplitList,
                consumerNamesList = consumerNamesList,
            )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataSplitService.setCheckState(
                orderDataSplitList = orderDataSplitList,
                position = position,
                state = state,
            )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun createOrderReport(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
        initialConsumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentConsumerList = orderDataSplitService.getAllConsumerNames(
                orderDataSplitList = orderDataSplitList,
                initialConsumerNamesList = initialConsumerNamesList,
            )
            val response = orderReportCreator.buildOrderReportForAll(
                receiptData = receiptData,
                orderDataSplitList = orderDataSplitList,
                consumerNameList = currentConsumerList,
            )
            setOrderReportText(response)
        }
    }

    private fun clearOrderReport(
        orderDataSplitList: List<OrderDataSplit>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList = orderDataSplitService.clearOrderDataSplits(
                orderDataSplitList = orderDataSplitList,
            )
            setOrderDataSplitList(newOrderDataCheckList)
        }
    }

    private fun setAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
        initialConsumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newConsumerNameList = orderDataSplitService.getAllConsumerNames(
                orderDataSplitList = orderDataSplitList,
                initialConsumerNamesList = initialConsumerNamesList,
            )
            setAllConsumerNamesList(newConsumerNameList)
        }
    }

    private fun setCheckStateExistence(
        orderDataSplitList: List<OrderDataSplit>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newIsCheckStateExisted = orderDataSplitService.hasExistingCheckState(
                orderDataSplitList = orderDataSplitList,
            )
            setCheckStateExistence(newIsCheckStateExisted)
        }
    }

    private fun saveOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val response = splitReceiptUseCase.saveOrderDataSplitList(
                orderDataSplitList = orderDataSplitList,
                orderDataList = orderDataList,
            )
            when (response) {
                is BasicFunResponse.Success -> {
                    setUiMessageIntent(SplitReceiptForAllUiMessageIntent.DataWasSaved)
                }

                is BasicFunResponse.Error -> {
                    setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
                }
            }
        }
    }

    private fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderSplitDataList = orderDataSplitService.clearAllConsumerNamesForSpecificOrder(
                orderDataSplitList = orderDataSplitList,
                position = position,
            )
            setOrderDataSplitList(newOrderSplitDataList)
        }
    }

    private fun addConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderSplitDataList = orderDataSplitService.addNewConsumerNameForSpecificOrder(
                orderDataSplitList = orderDataSplitList,
                position = position,
                name = name,
            )
            setOrderDataSplitList(newOrderSplitDataList)
        }
    }

    private fun addNewNameForAllConsumerNames(
        name: String,
        allConsumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val newAllConsumerNamesList = allConsumerNamesList.toMutableList()
                if (name !in allConsumerNamesList && allConsumerNamesList.size < DataConstantsReceipt.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES)
                    newAllConsumerNamesList.add(name)
                setAllConsumerNamesList(newAllConsumerNamesList.toList())
            }.getOrElse { e: Throwable ->
                setUiMessageIntent(SplitReceiptForAllUiMessageIntent.InternalError)
            }
        }
    }
}

sealed interface SplitReceiptForAllEvents : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptForAllEvents
    object ActivateOrderReportCreator : SplitReceiptForAllEvents
    class SetCheckState(val position: Int, val state: Boolean) : SplitReceiptForAllEvents
    class SetConsumerNames(val consumerNamesList: List<String>) : SplitReceiptForAllEvents
    class ClearConsumerNameForOrder(val position: Int, val name: String) : SplitReceiptForAllEvents
    object ClearOrderReport : SplitReceiptForAllEvents
    object SaveOrderDataSplit : SplitReceiptForAllEvents
    class ClearAllConsumerNames(val position: Int) : SplitReceiptForAllEvents
    class AddConsumerNameForSpecificOrder(val position: Int, val name: String) :
        SplitReceiptForAllEvents

    class AddNewNameForAllConsumerNames(val name: String) : SplitReceiptForAllEvents
}

interface SplitReceiptForAllUiState : BasicUiState {
    object Show : SplitReceiptForAllUiState
    object Loading : SplitReceiptForAllUiState
}

interface SplitReceiptForAllIntent : BasicIntent

interface SplitReceiptForAllUiMessageIntent : BasicUiMessageIntent {
    object DataWasSaved : SplitReceiptForAllUiMessageIntent
    object InternalError : SplitReceiptForAllUiMessageIntent
}