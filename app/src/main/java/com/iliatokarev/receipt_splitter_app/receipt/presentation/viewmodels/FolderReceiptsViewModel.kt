package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel
import com.iliatokarev.receipt_splitter_app.receipt.domain.ReceiptDataServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.FolderReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.ReportsUseCaseResponse
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FolderReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val receiptDataService: ReceiptDataServiceInterface,
    private val allFoldersUseCase: AllFoldersUseCaseInterface,
    private val folderReceiptsUseCase: FolderReceiptsUseCaseInterface,
) : BasicViewModel<FolderReceiptsUiState, FolderReceiptsIntent, FolderReceiptsEvent, FolderReceiptsUiMessageIntent>(
    FolderReceiptsUiState.Show

) {
    private val allReceiptsList = MutableStateFlow<List<ReceiptData>?>(null)
    private val allReceiptsListState = allReceiptsList.asStateFlow()

    private val isReportGenerationPending = MutableStateFlow(false)
    private val isReportGenerationPendingState = isReportGenerationPending.asStateFlow()

    private val folderData = MutableStateFlow<FolderData?>(null)
    private val folderDataState = folderData.asStateFlow()

    private val receiptReport = MutableStateFlow<ReceiptReports?>(null)
    private val receiptReportState = receiptReport.asStateFlow()

    private fun setAllReceiptsList(newList: List<ReceiptData>) {
        allReceiptsList.value = newList
    }

    private fun setIsReportGenerationPending(newBoolean: Boolean) {
        isReportGenerationPending.value = newBoolean
    }

    private fun setFolderData(newFolderData: FolderData?) {
        folderData.value = newFolderData
    }

    private fun setReceiptReports(newReports: ReceiptReports?) {
        receiptReport.value = newReports
    }

    fun getAllReceiptsList() = allReceiptsListState
    fun getIsReportGenerationPendingState() = isReportGenerationPendingState
    fun getFolderDataState() = folderDataState
    fun getReceiptReportState() = receiptReportState

    override fun setEvent(newEvent: FolderReceiptsEvent) {
        when (newEvent) {
            is FolderReceiptsEvent.RetrieveAllReceiptsForSpecificFolder -> {
                retrieveAllReceiptsForSpecificFolder(folderId = newEvent.folderId)
                monitorAllReceiptsChange()
            }

            is FolderReceiptsEvent.CreateFullOrdersReport -> {
                createAllReports(allReceiptsList.value ?: emptyList())
            }

            is FolderReceiptsEvent.MoveReceiptOutOfFolder -> {
                moveReceiptOutOfFolder(receiptData = newEvent.receiptData)
            }

            is FolderReceiptsEvent.ChangeShareStateForReceipt -> {
                changeSharedStateForReceipt(receiptData = newEvent.receiptData)
            }

            is FolderReceiptsEvent.ChangeCheckStateForSpecificReceipt -> {
                allReceiptsList.value?.let { receiptsList ->
                    changeCheckStateForSpecificReceipt(
                        receiptDataLists = receiptsList,
                        receiptId = newEvent.receiptId,
                    )
                }
            }

            is FolderReceiptsEvent.TurnOffCheckStateForAllReceipts -> {
                allReceiptsList.value?.let { receiptsList ->
                    turnOffCheckStateForAllReceipts(
                        receiptDataLists = receiptsList,
                    )
                }
            }

            is FolderReceiptsEvent.AddConsumerNameToFolder -> {
                folderData.value?.let { folderData ->
                    addConsumerNameToFolder(
                        folderData = folderData,
                        consumerName = newEvent.consumerName,
                    )
                }
            }

            is FolderReceiptsEvent.RetrieveFolderData -> {
                retrieveFolderData(folderId = newEvent.folderId)
            }

            is FolderReceiptsEvent.DeleteConsumerNameFromFolder -> {
                folderData.value?.let { folderData ->
                    deleteConsumerNameFromFolder(
                        folderData = folderData,
                        consumerName = newEvent.consumerName,
                    )
                }
            }
        }
    }

    private fun monitorAllReceiptsChange() {
        viewModelScope.launch {
            allReceiptsListState.collect { list ->
                if (list != null)
                    checkIfReportGenerationIsPending(receiptDataLists = list)
            }
        }
    }

    private fun retrieveAllReceiptsForSpecificFolder(
        folderId: Long,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.gelReceiptsByFolderIdFlow(folderId = folderId).collect { list ->
                setAllReceiptsList(list.reversed())
            }
        }
    }

    private fun retrieveFolderData(
        folderId: Long,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.getFolderByIdFlow(folderId = folderId).collect { folderData ->
                setFolderData(folderData)
            }
        }
    }

    private fun changeCheckStateForSpecificReceipt(
        receiptDataLists: List<ReceiptData>,
        receiptId: Long,
    ) {
        viewModelScope.launch {
            val newReceiptDataLists = receiptDataService.changeCheckStateForSpecificReceipt(
                receiptDataList = receiptDataLists,
                receiptId = receiptId,
            )
            setAllReceiptsList(newReceiptDataLists)
        }
    }

    private fun changeSharedStateForReceipt(
        receiptData: ReceiptData,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.changeSharedStateForReceipt(receiptData = receiptData)
        }
    }

    private fun moveReceiptOutOfFolder(
        receiptData: ReceiptData,
    ) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptOutFolder(receiptData = receiptData)
        }
    }

    private fun turnOffCheckStateForAllReceipts(
        receiptDataLists: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            val newReceiptDataLists = receiptDataService.turnOffCheckStateForAllReceipts(
                receiptDataList = receiptDataLists,
            )
            setAllReceiptsList(newReceiptDataLists)
        }
    }

    private fun checkIfReportGenerationIsPending(
        receiptDataLists: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            val booleanResponse =
                receiptDataService.checkIfReportGenerationIsPending(receiptDataList = receiptDataLists)
            setIsReportGenerationPending(newBoolean = booleanResponse)
        }
    }

    private fun addConsumerNameToFolder(
        folderData: FolderData,
        consumerName: String,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.addConsumerNameToFolder(
                folderData = folderData,
                consumerName = consumerName,
            )
        }
    }

    private fun deleteConsumerNameFromFolder(
        folderData: FolderData,
        consumerName: String,
    ) {
        viewModelScope.launch {
            allFoldersUseCase.deleteConsumerNameFromFolder(
                folderData = folderData,
                consumerName = consumerName,
            )
        }
    }

    private fun createAllReports(
        allReceiptsList: List<ReceiptData>,
    ) {
        viewModelScope.launch {
            val response = folderReceiptsUseCase.createAllReports(allReceiptsList = allReceiptsList)
            when (response) {
                is ReportsUseCaseResponse.Reports -> {
                    setReceiptReports(
                        ReceiptReports(
                            shortReport = response.shortReport,
                            longReport = response.longReport,
                            basicReport = response.basicReport,
                        )
                    )
                }

                is ReportsUseCaseResponse.Error -> {

                }
            }
        }
    }
}

class ReceiptReports(
    val shortReport: String,
    val longReport: String,
    val basicReport: String,
)

interface FolderReceiptsUiState : BasicUiState {
    object Show : FolderReceiptsUiState
    object Loading : FolderReceiptsUiState
}

interface FolderReceiptsIntent : BasicIntent

sealed interface FolderReceiptsEvent : BasicEvent {
    class RetrieveAllReceiptsForSpecificFolder(val folderId: Long) : FolderReceiptsEvent
    class ChangeCheckStateForSpecificReceipt(val receiptId: Long) : FolderReceiptsEvent
    object CreateFullOrdersReport : FolderReceiptsEvent
    class MoveReceiptOutOfFolder(val receiptData: ReceiptData) : FolderReceiptsEvent
    class ChangeShareStateForReceipt(val receiptData: ReceiptData) : FolderReceiptsEvent
    object TurnOffCheckStateForAllReceipts : FolderReceiptsEvent
    class AddConsumerNameToFolder(val consumerName: String) : FolderReceiptsEvent
    class RetrieveFolderData(val folderId: Long) : FolderReceiptsEvent
    class DeleteConsumerNameFromFolder(val consumerName: String) : FolderReceiptsEvent
}

interface FolderReceiptsUiMessageIntent : BasicUiMessageIntent
