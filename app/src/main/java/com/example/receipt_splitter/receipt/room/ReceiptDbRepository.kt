package com.example.receipt_splitter.receipt.room

import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiptDbRepository(
    private val receiptDao: ReceiptDao,
    private val receiptAdapter: ReceiptAdapter,
) : ReceiptDbRepositoryInterface {

    override suspend fun insertReceiptData(receiptDataJson: ReceiptDataJson) {
        val receiptEntity = receiptAdapter.transformReceiptDataToReceiptEntity(receiptDataJson)
        val receiptId = receiptDao.insertReceipt(receipt = receiptEntity)
        val orderEntityList = receiptAdapter.transformOrderListToOrderEntity(
                orderListData = receiptDataJson.orders,
                receiptId = receiptId,
            )
        receiptDao.insertOrders(orders = orderEntityList)
    }

    override suspend fun deleteReceiptData(receiptId: Long) {
        receiptDao.deleteReceipt(receiptId = receiptId)
    }

    override suspend fun getAllReceiptData(): Flow<List<SplitReceiptData>> {
        return receiptDao.getAllReceiptsWithOrders().map { list ->
            receiptAdapter.transformReceiptWithDataListToSplitReceiptDateList(list)
        }
    }
}

interface ReceiptDbRepositoryInterface {
    suspend fun insertReceiptData(receiptDataJson: ReceiptDataJson)
    suspend fun deleteReceiptData(receiptId: Long)
    suspend fun getAllReceiptData(): Flow<List<SplitReceiptData>>
}