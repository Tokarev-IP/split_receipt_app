package com.iliatokarev.receipt_splitter_app.receipt.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter_app.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter_app.main.basic.BasicSimpleViewModel
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllReceiptsUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.domain.usecases.AllFoldersUseCaseInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllReceiptsViewModel(
    private val allReceiptsUseCase: AllReceiptsUseCaseInterface,
    private val allFoldersUseCase: AllFoldersUseCaseInterface,
) : BasicSimpleViewModel<AllReceiptsEvent>() {

    private val allReceiptsList = MutableStateFlow<List<ReceiptData>?>(null)
    private val allReceiptsListState = allReceiptsList.asStateFlow()

    private val foldersListUnarchived = MutableStateFlow<List<FolderData>?>(null)
    private val foldersListUnarchivedState = foldersListUnarchived.asStateFlow()

    private val foldersListArchived = MutableStateFlow<List<FolderData>?>(null)
    private val foldersListArchivedState = foldersListArchived.asStateFlow()

    private fun setAllReceiptsList(newList: List<ReceiptData>) {
        allReceiptsList.value = newList
    }

    private fun setFoldersListUnarchived(newList: List<FolderData>) {
        foldersListUnarchived.value = newList
    }

    private fun setFoldersListArchived(newList: List<FolderData>) {
        foldersListArchived.value = newList
    }

    fun getAllReceiptsList() = allReceiptsListState
    fun getFoldersListUnarchived() = foldersListUnarchivedState
    fun getFoldersListArchived() = foldersListArchivedState

    override fun setEvent(newEvent: AllReceiptsEvent) {
        when (newEvent) {
            is AllReceiptsEvent.RetrieveAllData -> {
                retrieveAllReceipts()
                retrieveAllArchivedFolder()
                retrieveAllUnArchivedFolder()
            }

            is AllReceiptsEvent.DeleteSpecificReceipt -> {
                deleteReceiptData(receiptId = newEvent.receiptId)
            }

            is AllReceiptsEvent.MoveReceiptInFolder -> {
                moveReceiptInFolder(
                    receiptData = newEvent.receiptData,
                    folderId = newEvent.folderId,
                )
            }

            is AllReceiptsEvent.MoveReceiptOutFolder -> {
                moveReceiptOutFolder(receiptData = newEvent.receiptData)
            }

            is AllReceiptsEvent.SaveFolder -> {
                saveFolder(folderData = newEvent.folderData)
            }

            is AllReceiptsEvent.ArchiveFolder -> {
                archiveFolder(folderData = newEvent.folderData)
            }

            is AllReceiptsEvent.UnArchiveFolder -> {
                unArchiveFolder(folderData = newEvent.folderData)
            }
        }
    }

    private fun retrieveAllReceipts() {
        viewModelScope.launch(Dispatchers.IO) {
            allReceiptsUseCase.getAllReceiptsFlow().collect { data: List<ReceiptData> ->
                setAllReceiptsList(data.reversed())
            }
        }
    }

    private fun retrieveAllArchivedFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            allFoldersUseCase.getAllArchivedFoldersFlow().collect { list: List<FolderData> ->
                setFoldersListArchived(list.reversed())
            }
        }
    }

    private fun retrieveAllUnArchivedFolder() {
        viewModelScope.launch(Dispatchers.IO) {
            allFoldersUseCase.getAllUnarchivedFoldersFlow().collect { list: List<FolderData> ->
                setFoldersListUnarchived(list.reversed())
            }
        }
    }

    private fun deleteReceiptData(receiptId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.deleteReceiptData(receiptId = receiptId)
        }
    }

    private fun saveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.saveFolder(folderData)
        }
    }

    private fun moveReceiptInFolder(receiptData: ReceiptData, folderId: Long) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptInFolder(
                receiptData = receiptData,
                folderId = folderId,
            )
        }
    }

    private fun moveReceiptOutFolder(receiptData: ReceiptData) {
        viewModelScope.launch {
            allReceiptsUseCase.moveReceiptOutFolder(receiptData = receiptData)
        }
    }

    private fun archiveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.archiveFolder(folderData = folderData)
        }
    }

    private fun unArchiveFolder(folderData: FolderData) {
        viewModelScope.launch {
            allFoldersUseCase.unArchiveFolder(folderData = folderData)
        }
    }
}

sealed interface AllReceiptsEvent : BasicEvent {
    object RetrieveAllData : AllReceiptsEvent
    class DeleteSpecificReceipt(val receiptId: Long) : AllReceiptsEvent
    class SaveFolder(val folderData: FolderData) : AllReceiptsEvent
    class ArchiveFolder(val folderData: FolderData) : AllReceiptsEvent
    class UnArchiveFolder(val folderData: FolderData) : AllReceiptsEvent
    class MoveReceiptInFolder(val receiptData: ReceiptData, val folderId: Long) : AllReceiptsEvent
    class MoveReceiptOutFolder(val receiptData: ReceiptData) : AllReceiptsEvent
}