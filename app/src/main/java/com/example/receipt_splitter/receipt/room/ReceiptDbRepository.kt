package com.example.receipt_splitter.receipt.room

import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiptDbRepository(
    private val receiptDao: ReceiptDao,
    private val receiptAdapter: ReceiptAdapter,
) : ReceiptDbRepositoryInterface {

    override suspend fun insertReceiptData(receiptData: ReceiptData) {
        val receiptEntity = receiptAdapter.transformReceiptDataToReceiptEntity(receiptData)
        val receiptId = receiptDao.insertReceipt(receipt = receiptEntity)
        val orderEntityList = receiptAdapter.transformOrderListToOrderEntity(
                orderListData = receiptData.orders,
                receiptId = receiptId,
            )
        receiptDao.insertOrders(orders = orderEntityList)
    }

    override suspend fun deleteReceiptData(receiptId: Long) {
        receiptDao.deleteReceipt(receiptId = receiptId)
    }

    override suspend fun getAllReceiptData(): Flow<List<ReceiptData>> {
        return receiptDao.getAllReceiptsWithOrders().map { list ->
            receiptAdapter.transformReceiptWithDataListToReceiptDateList(list)
        }
    }
}

interface ReceiptDbRepositoryInterface {
    suspend fun insertReceiptData(receiptData: ReceiptData)
    suspend fun deleteReceiptData(receiptId: Long)
    suspend fun getAllReceiptData(): Flow<List<ReceiptData>>
}