package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel
import com.iliatokarev.receipt_splitter_app.receipt.domain.OrderDataSplitServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.reports.OrderReportCreatorInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.SplitReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitReceiptForAllViewModel(
    private val orderReportCreator: OrderReportCreatorInterface,
    private val splitReceiptUseCase: SplitReceiptUseCaseInterface,
    private val orderDataSplitService: OrderDataSplitServiceInterface,
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val allFolderUseCase: AllFoldersUseCaseInterface,
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

    private fun setSplitReceiptData(data: ReceiptData?) {
        splitReceiptDataFlow.value = data
    }


    private fun setOrderDataSplitList(list: List<OrderDataSplit>) {
        splitOrderDataSplitListFlow.value = list
    }

    private fun setOrderReportText(text: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            orderReportTextFlow.emit(text)
        }
    }

    private fun setAllConsumerNamesList(list: List<String>) {
        allConsumerNamesListFlow.value = list
    }

    private fun setCheckStateExistence(state: Boolean) {
        checkStateExistenceFlow.value = state
    }

    private fun setAssignedConsumerNamesList(list: List<String>) {
        assignedConsumerNamesList = list
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
                retrieveAllData(receiptId = newEvent.receiptId)
            }

            is SplitReceiptForAllEvents.SetCheckState -> {
                setCheckState(
                    orderDataSplitList = splitOrderDataCheckListState.value,
                    position = newEvent.position,
                    state = newEvent.state,
                )
            }

            is SplitReceiptForAllEvents.SetInitialConsumerNamesForCheckedOrders -> {
                setInitialConsumerNamesForCheckedOrders(
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

            is SplitReceiptForAllEvents.SetIsSharedStateForReceipt -> {
                setIsSharedStateForReceipt(newEvent.receiptData)
            }
        }
    }

    private fun retrieveAllData(receiptId: Long) {
        viewModelScope.launch {
            async { retrieveReceiptData(receiptId = receiptId) }.await()
            retrieveOrderDataSplitList(receiptId = receiptId)
        }
    }

    private suspend fun retrieveReceiptData(receiptId: Long) {
        splitReceiptUseCase.retrieveReceiptData(receiptId = receiptId).run {
            this?.folderId?.let { folderId ->
                retrieveFolderData(folderId)
            }
            setSplitReceiptData(this)
        }
    }

    private suspend fun retrieveFolderData(folderId: Long) {
        allFolderUseCase.getFolderById(folderId = folderId).run {
            setAssignedConsumerNamesList(this?.consumerNamesList ?: emptyList())
        }
    }

    private suspend fun retrieveOrderDataSplitList(receiptId: Long) {
        splitReceiptUseCase.retrieveOrderDataList(receiptId = receiptId).run {
            orderDataList = this
            val orderDataSplitList =
                orderDataSplitService.convertOrderDataListToOrderDataSplitList(
                    orderDataList = this
                )
            setOrderDataSplitList(orderDataSplitList)
        }
    }

    private fun monitorOrderDataCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            splitOrderDataSplitListFlow.collect { orderDataSplitList ->
                splitReceiptDataFlow.value?.let { receipt ->
                    createOrderReport(
                        receiptData = receipt,
                        orderDataSplitList = orderDataSplitList,
                    )
                }
                setAllConsumerNames(
                    orderDataSplitList = orderDataSplitList,
                    assignedConsumerNamesList = assignedConsumerNamesList,
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

    private fun setInitialConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newOrderDataCheckList =
                orderDataSplitService.setInitialConsumerNamesForCheckedOrders(
                    orderDataSplitList = orderDataSplitList,
                    consumerNamesList = consumerNamesList,
                )
            setOrderDataSplitList(newOrderDataCheckList)
            setAllConsumerNames(
                orderDataSplitList = newOrderDataCheckList,
                assignedConsumerNamesList = emptyList(),
            )
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
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val response = orderReportCreator.buildOrderReportForAll(
                receiptData = receiptData,
                orderDataSplitList = orderDataSplitList,
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
        assignedConsumerNamesList: List<String>,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newConsumerNameList = orderDataSplitService.getAllConsumerNames(
                orderDataSplitList = orderDataSplitList,
                assignedConsumerNamesList = assignedConsumerNamesList,
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

    private fun setIsSharedStateForReceipt(
        receiptData: ReceiptData
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            allReceiptsUseCase.insertReceiptData(
                receiptData = receiptData.copy(isShared = true)
            )
        }
    }
}

sealed interface SplitReceiptForAllEvents : BasicEvent {
    class RetrieveReceiptData(val receiptId: Long) : SplitReceiptForAllEvents
    object ActivateOrderReportCreator : SplitReceiptForAllEvents
    class SetCheckState(val position: Int, val state: Boolean) : SplitReceiptForAllEvents
    class SetInitialConsumerNamesForCheckedOrders(val consumerNamesList: List<String>) :
        SplitReceiptForAllEvents

    class ClearConsumerNameForOrder(val position: Int, val name: String) : SplitReceiptForAllEvents
    object ClearOrderReport : SplitReceiptForAllEvents
    object SaveOrderDataSplit : SplitReceiptForAllEvents
    class ClearAllConsumerNames(val position: Int) : SplitReceiptForAllEvents
    class AddConsumerNameForSpecificOrder(val position: Int, val name: String) :
        SplitReceiptForAllEvents

    class SetIsSharedStateForReceipt(val receiptData: ReceiptData) : SplitReceiptForAllEvents
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