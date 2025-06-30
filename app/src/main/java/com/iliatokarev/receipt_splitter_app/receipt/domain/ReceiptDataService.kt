package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReceiptDataService : ReceiptDataServiceInterface {

    override suspend fun changeCheckStateForSpecificReceipt(
        receiptDataList: List<ReceiptData>,
        receiptId: Long,
    ): List<ReceiptData> {
        return withContext(Dispatchers.Default) {
            runCatching {
                receiptDataList.map { receiptData ->
                    if (receiptData.id == receiptId) {
                        receiptData.copy(isChecked = !receiptData.isChecked)
                    } else {
                        receiptData
                    }
                }
            }.getOrElse { e: Throwable ->
                return@withContext receiptDataList
            }
        }
    }

    override suspend fun checkIfReportGenerationIsPending(
        receiptDataList: List<ReceiptData>
    ): Boolean {
        return withContext(Dispatchers.Default) {
            runCatching {
                receiptDataList.any { receiptData ->
                    receiptData.isChecked
                }
            }.getOrElse { e: Throwable ->
                false
            }
        }
    }

    override suspend fun turnOffCheckStateForAllReceipts(
        receiptDataList: List<ReceiptData>
    ): List<ReceiptData> {
        return withContext(Dispatchers.Default) {
            runCatching {
                receiptDataList.map { receiptData ->
                    receiptData.copy(isChecked = false)
                }
            }.getOrElse { e: Throwable ->
                return@withContext receiptDataList
            }
        }
    }
}

interface ReceiptDataServiceInterface {
    suspend fun changeCheckStateForSpecificReceipt(
        receiptDataList: List<ReceiptData>,
        receiptId: Long,
    ): List<ReceiptData>

    suspend fun checkIfReportGenerationIsPending(
        receiptDataList: List<ReceiptData>,
    ): Boolean

    suspend fun turnOffCheckStateForAllReceipts(
        receiptDataList: List<ReceiptData>,
    ): List<ReceiptData>
}