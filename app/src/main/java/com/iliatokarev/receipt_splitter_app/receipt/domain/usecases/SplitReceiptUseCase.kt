package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplitReceiptUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : SplitReceiptUseCaseInterface {

    override suspend fun retrieveReceiptData(receiptId: Long): ReceiptData? {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getReceiptDataById(id = receiptId)
            }.getOrNull()
        }
    }

    override suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getOrdersByReceiptId(receiptId = receiptId)
            }.getOrElse {
                return@withContext emptyList()
            }
        }
    }

}

interface SplitReceiptUseCaseInterface {
    suspend fun retrieveReceiptData(receiptId: Long): ReceiptData?
    suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData>
}