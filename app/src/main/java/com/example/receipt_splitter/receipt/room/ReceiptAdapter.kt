package com.example.receipt_splitter.receipt.room

import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReceiptAdapter: ReceiptAdapterInterface {

    override suspend fun transformReceiptWithDataListToReceiptDateList(
        receiptWithOrdersList: List<ReceiptWithOrders>,
    ): List<ReceiptData> {
        return suspendCoroutine { continuation ->
            val receiptDataList = mutableListOf<ReceiptData>()
            for (receiptWithOrders in receiptWithOrdersList) {
                val ordersList = mutableListOf<OrderData>()
                for (order in receiptWithOrders.orders) {
                    ordersList.add(
                        OrderData(
                            name = order.name,
                            quantity = order.quantity,
                            price = order.price,
                        )
                    )
                }

                val receiptData = ReceiptData(
                    id = receiptWithOrders.receipt.id,
                    restaurant = receiptWithOrders.receipt.restaurant,
                    date = receiptWithOrders.receipt.date,
                    orders = ordersList,
                    subTotal = receiptWithOrders.receipt.subTotal,
                    total = receiptWithOrders.receipt.total,
                    tax = receiptWithOrders.receipt.tax,
                    discount = receiptWithOrders.receipt.discount,
                    tip = receiptWithOrders.receipt.tip,
                    tipSum = receiptWithOrders.receipt.tipSum,
                )
                receiptDataList.add(receiptData)
            }

            continuation.resume(receiptDataList)
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity {
        return suspendCoroutine { continuation ->
            val receiptEntity = ReceiptEntity(
                restaurant = receiptData.restaurant,
                date = receiptData.date,
                subTotal = receiptData.subTotal,
                total = receiptData.total,
                tax = receiptData.tax,
                discount = receiptData.discount,
                tip = receiptData.tip,
                tipSum = receiptData.tipSum,
            )
            continuation.resume(receiptEntity)
        }
    }

    override suspend fun transformOrderListToOrderEntity(
        orderListData: List<OrderData>,
        receiptId: Long,
    ): List<OrderEntity> {
        return suspendCoroutine { continuation ->
            val orderEntityList = mutableListOf<OrderEntity>()
            for (order in orderListData) {
                orderEntityList.add(
                    OrderEntity(
                        name = order.name,
                        quantity = order.quantity,
                        price = order.price,
                        receiptId = receiptId,
                    )
                )
            }
            continuation.resume(orderEntityList)
        }
    }
}

interface ReceiptAdapterInterface {
    suspend fun transformReceiptWithDataListToReceiptDateList(
        receiptWithOrdersList: List<ReceiptWithOrders>,
    ): List<ReceiptData>

    suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity

    suspend fun transformOrderListToOrderEntity(
        orderListData: List<OrderData>,
        receiptId: Long,
    ): List<OrderEntity>
}