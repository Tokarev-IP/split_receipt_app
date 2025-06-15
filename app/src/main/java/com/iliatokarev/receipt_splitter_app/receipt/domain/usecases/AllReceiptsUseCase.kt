package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class AllReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface
) : AllReceiptsUseCaseInterface {

    override suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getAllReceiptDataFlow()
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
        TODO("Not yet implemented")
    }

    override suspend fun moveReceiptOutFolder(receiptData: ReceiptData): BasicFunResponse {
        TODO("Not yet implemented")
    }
}

interface AllReceiptsUseCaseInterface {
    suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>>
    suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse
    suspend fun moveReceiptInFolder(receiptData: ReceiptData, folderId: Long): BasicFunResponse
    suspend fun moveReceiptOutFolder(receiptData: ReceiptData): BasicFunResponse
}