package com.iliatokarev.receipt_splitter.receipt.domain.usecases

import com.iliatokarev.receipt_splitter.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter.receipt.data.room.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class AllReceiptsUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface
) : AllReceiptsUseCaseInterface {

    override suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>> {
        return receiptDbRepository.getAllReceiptDataFlow()
            .catch { emit(emptyList()) }
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
}

interface AllReceiptsUseCaseInterface {
    suspend fun getAllReceiptsFlow(): Flow<List<ReceiptData>>
    suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse
}