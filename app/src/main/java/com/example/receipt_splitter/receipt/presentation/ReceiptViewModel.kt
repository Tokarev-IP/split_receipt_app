package com.example.receipt_splitter.receipt.presentation

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.main.basic.BasicViewModel
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCaseResponse
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorUseCaseInterface
import com.example.receipt_splitter.receipt.domain.ReceiptDataConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.RoomReceiptUseCaseInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReceiptViewModel(
    private val imageReceiptConverterUseCase: ImageReceiptConverterUseCaseInterface,
    private val receiptDataConverterUseCase: ReceiptDataConverterUseCaseInterface,
    private val orderReportCreatorUseCase: OrderReportCreatorUseCaseInterface,
    private val roomReceiptUseCase: RoomReceiptUseCaseInterface,
) : BasicViewModel<
        ReceiptUiState,
        ReceiptIntent,
        ReceiptEvent,
        ReceiptNavigationEvent,
        ReceiptUiMessageIntent>(
    initialUiState = ReceiptUiState.Show,
) {
    private val splitOrderDataFlow = MutableStateFlow<List<SplitOrderData>>(emptyList())
    private val splitOrderDataState = splitOrderDataFlow.asStateFlow()

    private val orderReportTextFlow = MutableStateFlow<String?>(null)
    private val orderReportTextState = orderReportTextFlow.asStateFlow()

    private val allReceiptsList = MutableStateFlow<List<SplitReceiptData>>(emptyList())
    private val allReceiptsListState = allReceiptsList.asStateFlow()

    private val splitReceiptDataFlow = MutableStateFlow<SplitReceiptData?>(null)
    private val splitReceiptDataState = splitReceiptDataFlow.asStateFlow()

    private fun setSplitOrderDataList(newSplitReceiptItems: List<SplitOrderData>) {
        splitOrderDataFlow.value = newSplitReceiptItems
    }

    private fun setOrderReportText(newOrderReportText: String?) {
        orderReportTextFlow.value = newOrderReportText
    }

    private fun setAllReceiptsList(newList: List<SplitReceiptData>) {
        allReceiptsList.value = newList
    }

    private fun setSplitReceiptData(newSplitReceiptData: SplitReceiptData) {
        splitReceiptDataFlow.value = newSplitReceiptData
    }

    fun getSplitReceiptItems() = splitOrderDataState
    fun getOrderReportText() = orderReportTextState
    fun getAllReceiptsList() = allReceiptsListState
    fun getReceiptData() = splitReceiptDataState

    override fun setUiEvent(newUiEvent: ReceiptEvent) {
        when (newUiEvent) {
            is ReceiptEvent.ConvertImagesToReceipt -> {
                setUiState(ReceiptUiState.Loading)
                convertReceiptFromImage(newUiEvent.listOfImages)
            }

            is ReceiptEvent.AddQuantityToSplitOrderData -> {
                addQuantityToSplitOrderData(newUiEvent.orderId, splitOrderDataFlow.value)
            }

            is ReceiptEvent.SubtractQuantityToSplitOrderData -> {
                subtractQuantityToSplitOrderData(newUiEvent.orderId, splitOrderDataFlow.value)
            }

            is ReceiptEvent.AddNewReceipt -> {
                setIntent(ReceiptIntent.GoToChoosePhotoScreen)
            }

            is ReceiptEvent.ReceiptDeletion -> {
                removeReceipt(newUiEvent.receiptId)
            }

            is ReceiptEvent.RetrieveAllReceipts -> {
                setUiState(ReceiptUiState.Loading)
                retrieveAllReceipts()
            }

            is ReceiptEvent.OpenSplitReceiptScreen -> {
                setSplitReceiptData(newUiEvent.splitReceiptData)
                setSplitOrderDataList(newUiEvent.splitReceiptData.orders)
                setOrderReportText(null)
                setIntent(ReceiptIntent.GoToSplitReceiptScreen)
            }

            is ReceiptEvent.SetShowState -> {
                setUiState(ReceiptUiState.Show)
            }
        }
    }

    override fun setNavigationEvent(newNavigationEvent: ReceiptNavigationEvent) {
        TODO("Not yet implemented")
    }

    private fun convertReceiptFromImage(listOfImages: List<Uri>) {
        viewModelScope.launch {
            val response: ImageReceiptConverterUseCaseResponse =
                imageReceiptConverterUseCase.convertReceiptFromImage(listOfImages = listOfImages)
            when (response) {
                is ImageReceiptConverterUseCaseResponse.ImageIsInappropriate -> {
                    setUiState(ReceiptUiState.Show)
                    setUiMessageIntent(ReceiptUiMessageIntent.ImageIsInappropriate)
                }

                is ImageReceiptConverterUseCaseResponse.JsonError -> {
                    setUiState(ReceiptUiState.Show)
                }

                is ImageReceiptConverterUseCaseResponse.Success -> {
                    setUiState(ReceiptUiState.Show)
//                    setSplitReceiptData(response.receiptData)
//                    setSplitReceiptItems(response.receiptData.toSplitReceiptDataList())
//                    setIntent(ReceiptIntent.GoToSplitReceiptScreen)
//                    addNewReceipt(response.receiptData)
                }

                is ImageReceiptConverterUseCaseResponse.Error -> {
                    setUiState(ReceiptUiState.Show)
                    setUiMessageIntent(ReceiptUiMessageIntent.ReceiptMessage(msg = response.msg))
                }
            }
        }
    }

    private fun addQuantityToSplitOrderData(orderId: Long, list: List<SplitOrderData>) {
        viewModelScope.launch {
            val newList = receiptDataConverterUseCase.addQuantityToSplitOrderData(
                splitOrderDataList = list,
                orderId = orderId,
            )
            setSplitOrderDataList(newList)
            buildOrderReport()
        }
    }

    private fun subtractQuantityToSplitOrderData(orderId: Long, list: List<SplitOrderData>) {
        viewModelScope.launch {
            val newList = receiptDataConverterUseCase.subtractQuantityToSplitOrderData(
                splitOrderDataList = list,
                orderId = orderId,
            )
            setSplitOrderDataList(newList)
            buildOrderReport()
        }
    }

    private fun buildOrderReport() {
        viewModelScope.launch {
            splitReceiptDataState.value?.let { data ->
                val newOrderReportText = orderReportCreatorUseCase.buildOrderReport(
                    receiptData = data,
                    splitOrderDataList = splitOrderDataFlow.value,
                )
                setOrderReportText(newOrderReportText)
            } ?: setOrderReportText(null)
        }
    }

    private fun retrieveAllReceipts() {
        viewModelScope.launch {
            roomReceiptUseCase.getAllReceipts().collect { list ->
                setAllReceiptsList(list.reversed())
            }
        }
    }

    private fun removeReceipt(receiptId: Long) {
        viewModelScope.launch {
            val response: BasicFunResponse =
                roomReceiptUseCase.deleteReceipt(receiptId = receiptId)
            when (response) {
                is BasicFunResponse.Success -> {}
                is BasicFunResponse.Error -> {}
            }
        }
    }
}