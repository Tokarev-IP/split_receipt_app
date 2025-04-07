package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData
import com.example.receipt_splitter.receipt.room.ReceiptDbRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoomReceiptUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : RoomReceiptUseCaseInterface {

    override suspend fun getAllReceipts(): Flow<List<SplitReceiptData>> {
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
            onFailure = { e -> BasicFunResponse.Error(e.message ?: "An error") }
        )
    }

    override suspend fun deleteReceipt(receiptId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepository.deleteReceiptData(receiptId = receiptId)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e -> BasicFunResponse.Error(e.message ?: "An error") }
        )
    }
}

interface RoomReceiptUseCaseInterface {
    suspend fun getAllReceipts(): Flow<List<SplitReceiptData>>
    suspend fun addNewReceipt(receiptDataJson: ReceiptDataJson): BasicFunResponse
    suspend fun deleteReceipt(receiptId: Long): BasicFunResponse
}