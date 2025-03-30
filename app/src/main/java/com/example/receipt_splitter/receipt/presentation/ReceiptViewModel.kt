package com.example.receipt_splitter.receipt.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.main.basic.BasicViewModel
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.ImageReceiptConverterUseCaseResponse
import com.example.receipt_splitter.receipt.domain.OrderReportCreatorUseCaseInterface
import com.example.receipt_splitter.receipt.domain.ReceiptDataConverterUseCaseInterface
import com.example.receipt_splitter.receipt.domain.RoomReceiptUseCaseInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val receiptDataFlow = MutableStateFlow<ReceiptData>(ReceiptData())
    private val receiptDataState = receiptDataFlow.asStateFlow()

    private val splitReceiptItemsFlow = MutableStateFlow<List<SplitOrderData>>(emptyList())
    private val splitReceiptItemsState = splitReceiptItemsFlow.asStateFlow()

    private val orderReportTextFlow = MutableStateFlow<String?>(null)
    private val orderReportTextState = orderReportTextFlow.asStateFlow()

    private val allReceiptsList = MutableStateFlow<List<ReceiptData>>(emptyList())
    private val allReceiptsListState = allReceiptsList.asStateFlow()

    private fun setReceiptData(newReceiptData: ReceiptData) {
        receiptDataFlow.value = newReceiptData
    }

    private fun setSplitReceiptItems(newSplitReceiptItems: List<SplitOrderData>) {
        splitReceiptItemsFlow.value = newSplitReceiptItems
    }

    private fun setOrderReportText(newOrderReportText: String?) {
        orderReportTextFlow.value = newOrderReportText
    }

    private fun setAllReceiptsList(newList: List<ReceiptData>) {
        allReceiptsList.value = newList
    }

    fun getReceiptData() = receiptDataState
    fun getSplitReceiptItems() = splitReceiptItemsState
    fun getOrderReportText() = orderReportTextState
    fun getAllReceiptsList() = allReceiptsListState

    override fun setUiEvent(newUiEvent: ReceiptUiEvent) {
        when (newUiEvent) {
            is ReceiptUiEvent.ConvertReceiptFromImage -> {
                setUiState(ReceiptUiState.Loading)
                convertReceiptFromImage(newUiEvent.imageUri)
            }

            is ReceiptUiEvent.AddQuantityToSplitOrderData -> {
                addQuantityToSplitOrderData(newUiEvent.orderName, splitReceiptItemsFlow.value)
            }

            is ReceiptUiEvent.SubtractQuantityToSplitOrderData -> {
                subtractQuantityToSplitOrderData(newUiEvent.orderName, splitReceiptItemsFlow.value)
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
                setReceiptData(newUiEvent.receiptData)
                setSplitReceiptItems(newUiEvent.receiptData.toSplitReceiptDataList())
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
                }

                is ImageReceiptConverterUseCaseResponse.JsonError -> {
                    setUiState(ReceiptUiState.Show)
                }

                is ImageReceiptConverterUseCaseResponse.Success -> {
                    setReceiptData(response.receiptData)
                    setSplitReceiptItems(response.receiptData.toSplitReceiptDataList())
                    setIntent(ReceiptIntent.GoToSplitReceiptScreen)
                    addNewReceipt(response.receiptData)
                }

                is ImageReceiptConverterUseCaseResponse.Error -> {
                    setUiState(ReceiptUiState.Show)
                    setUiErrorIntent(ReceiptUiErrorIntent.ReceiptError(msg = response.msg))
                }
            }
        }
    }

    private fun addQuantityToSplitOrderData(orderName: String, list: List<SplitOrderData>) {
        viewModelScope.launch {
            val newList = receiptDataConverterUseCase.addQuantityToSplitOrderData(
                splitOrderDataList = list,
                orderName = orderName,
            )

            setSplitReceiptItems(newList)
            buildOrderReport()
        }
    }

    private fun subtractQuantityToSplitOrderData(orderName: String, list: List<SplitOrderData>) {
        viewModelScope.launch {
            val newList = receiptDataConverterUseCase.subtractQuantityToSplitOrderData(
                splitOrderDataList = list,
                orderName = orderName,
            )
            setSplitReceiptItems(newList)
            buildOrderReport()
        }
    }

    private fun buildOrderReport() {
        viewModelScope.launch {
            val newOrderReportText = withContext(Dispatchers.IO) {
                orderReportCreatorUseCase.buildOrderReport(
                    receiptData = receiptDataFlow.value,
                    splitOrderDataList = splitReceiptItemsFlow.value,
                )
            }
            setOrderReportText(newOrderReportText)
        }
    }

    private fun retrieveAllReceipts() {
        viewModelScope.launch {
            roomReceiptUseCase.getAllReceipts().collect { list ->
                setAllReceiptsList(list)
            }
        }
    }

    private fun addNewReceipt(receiptData: ReceiptData) {
        viewModelScope.launch {
            val response: BasicFunResponse =
                roomReceiptUseCase.addNewReceipt(receiptData = receiptData)
            when (response) {
                is BasicFunResponse.onSuccess -> {}
                is BasicFunResponse.onError -> {}
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