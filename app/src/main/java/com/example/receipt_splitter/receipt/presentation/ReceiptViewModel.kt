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
        ReceiptUiEvent,
        ReceiptUiErrorIntent>(
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

    override fun setUiEvent(newUiEvent: ReceiptUiEvent) {
        when (newUiEvent) {
            is ReceiptUiEvent.ConvertReceiptFromImage -> {
                setUiState(ReceiptUiState.Loading)
                convertReceiptFromImage(newUiEvent.imageUri)
            }

            is ReceiptUiEvent.AddQuantityToSplitOrderData -> {
                addQuantityToSplitOrderData(newUiEvent.orderId, splitOrderDataFlow.value)
            }

            is ReceiptUiEvent.SubtractQuantityToSplitOrderData -> {
                subtractQuantityToSplitOrderData(newUiEvent.orderId, splitOrderDataFlow.value)
            }

            is ReceiptUiEvent.AddNewReceipt -> {
                setIntent(ReceiptIntent.GoToChoosePhotoScreen)
            }

            is ReceiptUiEvent.ReceiptDeletion -> {
                removeReceipt(newUiEvent.receiptId)
            }

            is ReceiptUiEvent.RetrieveAllReceipts -> {
                setUiState(ReceiptUiState.Loading)
                retrieveAllReceipts()
            }

            is ReceiptUiEvent.OpenSplitReceiptScreen -> {
                setSplitReceiptData(newUiEvent.splitReceiptData)
                setSplitOrderDataList(newUiEvent.splitReceiptData.orders)
                setOrderReportText(null)
                setIntent(ReceiptIntent.GoToSplitReceiptScreen)
            }

            is ReceiptUiEvent.SetShowState -> {
                setUiState(ReceiptUiState.Show)
            }
        }
    }

    private fun convertReceiptFromImage(image: Uri) {
        viewModelScope.launch {
            val response: ImageReceiptConverterUseCaseResponse =
                imageReceiptConverterUseCase.convertReceiptFromImage(image = image)
            when (response) {
                is ImageReceiptConverterUseCaseResponse.ImageIsInappropriate -> {
                    setUiState(ReceiptUiState.Show)
                    setUiErrorIntent(ReceiptUiErrorIntent.ImageIsInappropriate)
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
                    setUiErrorIntent(ReceiptUiErrorIntent.ReceiptError(msg = response.msg))
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
                is BasicFunResponse.onSuccess -> {}
                is BasicFunResponse.onError -> {}
            }
        }
    }

}