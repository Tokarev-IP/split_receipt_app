package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptWithFolderData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class AllReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface
) : AllReceiptsUseCaseInterface {

    override suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getAllReceiptsFlow()
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun gelReceiptsByFolderIdFlow(folderId: Long): Flow<List<ReceiptData>> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getReceiptsByFolderIdFlow(folderId = folderId)
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun getAllReceiptsWithFolderFlow(): Flow<List<ReceiptWithFolderData>> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getReceiptsWithFolderFlow()
                .catch { emit(emptyList()) }
        }
    }

    override suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepository.deleteReceiptData(receiptId = receiptId)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(
                    e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
                )
            }
        )
    }

    override suspend fun moveReceiptInFolder(
        receiptData: ReceiptData,
        folderId: Long
    ): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newReceiptData = receiptData.copy(folderId = folderId)
                receiptDbRepository.insertReceiptData(receiptData = newReceiptData)
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

    override suspend fun moveReceiptOutFolder(
        receiptData: ReceiptData
    ): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newReceiptData = receiptData.copy(folderId = null)
                receiptDbRepository.insertReceiptData(receiptData = newReceiptData)
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

    override suspend fun changeIsSharedStateForReceipt(
        receiptData: ReceiptData
    ): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val newReceiptData = receiptData.copy(isShared = !receiptData.isShared)
                receiptDbRepository.insertReceiptData(receiptData = newReceiptData)
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

    override suspend fun changeIsSharedStateForAllReceipts(
        receiptDataList: List<ReceiptData>
    ): BasicFunResponse = withContext(Dispatchers.IO) {
        runCatching {
            for (receiptData in receiptDataList) {
                if (receiptData.isChecked) {
                    val newReceiptData = receiptData.copy(isShared = true)
                    receiptDbRepository.insertReceiptData(receiptData = newReceiptData)
                }
            }
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

interface AllReceiptsUseCaseInterface {
    suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>>
    suspend fun gelReceiptsByFolderIdFlow(folderId: Long): Flow<List<ReceiptData>>
    suspend fun getAllReceiptsWithFolderFlow(): Flow<List<ReceiptWithFolderData>>
    suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse
    suspend fun moveReceiptInFolder(receiptData: ReceiptData, folderId: Long): BasicFunResponse
    suspend fun moveReceiptOutFolder(receiptData: ReceiptData): BasicFunResponse
    suspend fun changeIsSharedStateForReceipt(receiptData: ReceiptData): BasicFunResponse
    suspend fun changeIsSharedStateForAllReceipts(receiptDataList: List<ReceiptData>): BasicFunResponse
}