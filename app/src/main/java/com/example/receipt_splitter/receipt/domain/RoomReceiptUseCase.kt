package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.presentation.ReceiptUiMessage
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.data.room.ReceiptDbRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoomReceiptUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : RoomReceiptUseCaseInterface {

    override suspend fun getAllReceipts(): Flow<List<ReceiptData>> {
        return withContext(Dispatchers.IO) {
            receiptDbRepository.getAllReceiptData()
        }
    }

    override suspend fun addNewReceipt(receiptDataJson: ReceiptDataJson): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepository.insertReceiptData(receiptDataJson)
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

    override suspend fun deleteReceipt(receiptId: Long): BasicFunResponse {
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

interface RoomReceiptUseCaseInterface {
    suspend fun getAllReceipts(): Flow<List<ReceiptData>>
    suspend fun addNewReceipt(receiptDataJson: ReceiptDataJson): BasicFunResponse
    suspend fun deleteReceipt(receiptId: Long): BasicFunResponse
}