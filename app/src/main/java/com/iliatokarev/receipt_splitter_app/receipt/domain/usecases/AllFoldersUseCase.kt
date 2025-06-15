package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
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

    override suspend fun getAllArchivedFoldersFlow(): Flow<List<FolderData>> {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getFoldersByArchived(isArchived = true)
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun getAllUnarchivedFoldersFlow(): Flow<List<FolderData>> {
        return withContext(Dispatchers.IO) {
            folderDbRepository.getFoldersByArchived(isArchived = false)
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

    override suspend fun deleteFolder(folderId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                folderDbRepository.deleteFolder(id = folderId)
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
    suspend fun getAllArchivedFoldersFlow(): Flow<List<FolderData>>
    suspend fun getAllUnarchivedFoldersFlow(): Flow<List<FolderData>>
    suspend fun saveFolder(folderData: FolderData): BasicFunResponse
    suspend fun archiveFolder(folderData: FolderData): BasicFunResponse
    suspend fun unArchiveFolder(folderData: FolderData): BasicFunResponse
    suspend fun deleteFolder(folderId: Long): BasicFunResponse
}