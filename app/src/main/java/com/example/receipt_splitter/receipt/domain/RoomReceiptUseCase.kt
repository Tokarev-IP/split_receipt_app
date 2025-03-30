package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.room.ReceiptDbRepositoryInterface
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

    override suspend fun addNewReceipt(receiptData: ReceiptData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepository.insertReceiptData(receiptData)
            }
        }.fold(
            onSuccess = { BasicFunResponse.onSuccess },
            onFailure = { e -> BasicFunResponse.onError(e.message ?: "An error") }
        )
    }

    override suspend fun deleteReceipt(receiptId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepository.deleteReceiptData(receiptId = receiptId)
            }
        }.fold(
            onSuccess = { BasicFunResponse.onSuccess },
            onFailure = { e -> BasicFunResponse.onError(e.message ?: "An error") }
        )
    }
}

interface RoomReceiptUseCaseInterface {
    suspend fun getAllReceipts(): Flow<List<ReceiptData>>
    suspend fun addNewReceipt(receiptData: ReceiptData): BasicFunResponse
    suspend fun deleteReceipt(receiptId: Long): BasicFunResponse
}