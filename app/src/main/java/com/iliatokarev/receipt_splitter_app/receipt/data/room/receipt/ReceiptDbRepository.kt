package com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt

import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptDataJson
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReceiptDbRepository(
    private val receiptDao: ReceiptDao,
    private val receiptAdapter: ReceiptAdapter,
) : ReceiptDbRepositoryInterface {

    override suspend fun insertReceiptDataJson(receiptDataJson: ReceiptDataJson): Long {
        val receiptEntity = receiptAdapter.transformReceiptDataJsonToReceiptEntity(receiptDataJson)
        val receiptId = receiptDao.insertReceipt(receipt = receiptEntity)
        val orderEntityList = receiptAdapter.transformOrderDataJsonListToOrderEntityList(
                orderDataJsonList = receiptDataJson.orders,
                receiptId = receiptId,
            )
        receiptDao.insertOrders(orders = orderEntityList)
        return receiptId
    }

    override suspend fun insertReceiptData(receiptData: ReceiptData) {
        val receiptEntity = receiptAdapter.transformReceiptDataToReceiptEntity(receiptData)
        receiptDao.insertReceipt(receipt = receiptEntity)
    }

    override suspend fun insertOrderData(orderData: OrderData) {
        val orderEntity = receiptAdapter.transformOrderDataToOrderEntity(orderData)
        receiptDao.insertOrder(order = orderEntity)
    }

    override suspend fun insertNewOrderData(orderData: OrderData) {
        val orderEntity = receiptAdapter.transformOrderDataToNewOrderEntity(orderData)
        receiptDao.insertOrder(order = orderEntity)
    }

    override suspend fun insertOrderDataLists(orderDataList: List<OrderData>) {
        val orderEntityList =
            receiptAdapter.transformOrderDataListToOrderEntityList(orderDataList = orderDataList)
        receiptDao.insertOrders(orders = orderEntityList)
    }

    override suspend fun getAllReceiptDataFlow(): Flow<List<ReceiptData>> {
        return receiptDao.getAllReceiptsFlow().map { receiptEntityList ->
            receiptAdapter.transformReceiptEntityListToReceiptDateList(receiptEntityList)
        }
    }

    override suspend fun getOrdersByReceiptIdFlow(receiptId: Long): Flow<List<OrderData>> {
        return receiptDao.getOrdersByReceiptIdFlow(receiptId).map { list ->
            receiptAdapter.transformOrderEntityListToOrderDataList(list)
        }
    }

    override suspend fun getOrdersByReceiptId(receiptId: Long): List<OrderData> {
        return receiptDao.getOrdersByReceiptId(receiptId = receiptId).run {
            receiptAdapter.transformOrderEntityListToOrderDataList(this)
        }
    }

    override suspend fun getReceiptDataByIdFlow(id: Long): Flow<ReceiptData?> {
        return receiptDao.getReceiptByIdFlow(receiptId = id).map { receiptEntity ->
            receiptEntity ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
            receiptAdapter.transformReceiptEntityToReceiptDate(receiptEntity)
        }
    }

    override suspend fun getReceiptDataById(id: Long): ReceiptData? {
        return receiptDao.getReceiptById(receiptId = id)?.let { receiptEntity ->
            receiptAdapter.transformReceiptEntityToReceiptDate(receiptEntity)
        } ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }

    override suspend fun deleteReceiptData(receiptId: Long) {
        receiptDao.deleteReceipt(receiptId = receiptId)
    }

    override suspend fun deleteOrderDataById(id: Long) {
        receiptDao.deleteOrder(orderId = id)
    }
}

interface ReceiptDbRepositoryInterface {
    suspend fun insertReceiptDataJson(receiptDataJson: ReceiptDataJson): Long
    suspend fun insertReceiptData(receiptData: ReceiptData)
    suspend fun insertOrderData(orderData: OrderData)
    suspend fun insertNewOrderData(orderData: OrderData)
    suspend fun insertOrderDataLists(orderDataList: List<OrderData>)
    suspend fun getAllReceiptDataFlow(): Flow<List<ReceiptData>>
    suspend fun getOrdersByReceiptIdFlow(receiptId: Long): Flow<List<OrderData>>
    suspend fun getOrdersByReceiptId(receiptId: Long): List<OrderData>
    suspend fun getReceiptDataByIdFlow(id: Long): Flow<ReceiptData?>
    suspend fun getReceiptDataById(id: Long): ReceiptData?
    suspend fun deleteReceiptData(receiptId: Long)
    suspend fun deleteOrderDataById(id: Long)
}