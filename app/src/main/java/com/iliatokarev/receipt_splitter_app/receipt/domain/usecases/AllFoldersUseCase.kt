package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderWithReceiptsData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class AllFoldersUseCase(
    private val folderDbRepository: FolderDbRepositoryInterface
) : AllFoldersUseCaseInterface {

    override suspend fun getAllFoldersFlow(): Flow<List<FolderData>> {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getAllFoldersFlow()
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun getFolderByIdFlow(folderId: Long): Flow<FolderData?> {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getFolderByIdFlow(id = folderId)
                .catch { emit(null) }
        }
    }

    override suspend fun getFolderById(folderId: Long): FolderData? {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getFolderById(id = folderId)
        }
    }

    override suspend fun getFoldersWithReceiptsFlow(): Flow<List<FolderWithReceiptsData>> {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getFoldersWithReceipts()
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun saveFolder(folderData: FolderData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                folderDbRepository.insertFolder(folderData = folderData)
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }

    override suspend fun archiveFolder(folderData: FolderData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                folderDbRepository.insertFolder(
                    folderData = folderData.copy(isArchived = true)
                )
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }

    override suspend fun unArchiveFolder(folderData: FolderData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                folderDbRepository.insertFolder(
                    folderData = folderData.copy(isArchived = false)
                )
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }

    override suspend fun deleteFolderById(folderId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                folderDbRepository.deleteFolderById(id = folderId)
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }

    override suspend fun deleteConsumerNameFromFolder(
        folderData: FolderData,
        consumerName: String
    ): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newConsumerNamesList = folderData.consumerNamesList
                    .filter { name ->
                        name != consumerName
                    }
                folderDbRepository.insertFolder(
                    folderData.copy(consumerNamesList = newConsumerNamesList)
                )
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }

    override suspend fun addConsumerNameToFolder(
        folderData: FolderData,
        consumerName: String
    ): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newConsumerNamesList = folderData.consumerNamesList
                    .toMutableList()
                    .apply { add(consumerName) }
                folderDbRepository.insertFolder(
                    folderData = folderData.copy(consumerNamesList = newConsumerNamesList)
                )
            }.fold(
                onSuccess = { BasicFunResponse.Success },
                onFailure = { e ->
                    BasicFunResponse.Error(
                        e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                    )
                }
            )
        }
    }
}

interface AllFoldersUseCaseInterface {
    suspend fun getAllFoldersFlow(): Flow<List<FolderData>>
    suspend fun getFolderByIdFlow(folderId: Long): Flow<FolderData?>
    suspend fun getFolderById(folderId: Long): FolderData?
    suspend fun getFoldersWithReceiptsFlow(): Flow<List<FolderWithReceiptsData>>
    suspend fun saveFolder(folderData: FolderData): BasicFunResponse
    suspend fun archiveFolder(folderData: FolderData): BasicFunResponse
    suspend fun unArchiveFolder(folderData: FolderData): BasicFunResponse
    suspend fun deleteFolderById(folderId: Long): BasicFunResponse
    suspend fun deleteConsumerNameFromFolder(
        folderData: FolderData,
        consumerName: String
    ): BasicFunResponse

    suspend fun addConsumerNameToFolder(
        folderData: FolderData,
        consumerName: String
    ): BasicFunResponse
}