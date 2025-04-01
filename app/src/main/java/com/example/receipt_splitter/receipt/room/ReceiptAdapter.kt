package com.example.receipt_splitter.receipt.room

import com.example.receipt_splitter.receipt.presentation.OrderDataJson
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.presentation.SplitOrderData
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData

class ReceiptAdapter : ReceiptAdapterInterface {

    override suspend fun transformReceiptWithDataListToSplitReceiptDateList(
        receiptWithOrdersList: List<ReceiptWithOrders>,
    ): List<SplitReceiptData> {
        return receiptWithOrdersList.map { receiptWithOrders ->
            val splitOrdersList = receiptWithOrders.orders.map { order ->
                SplitOrderData(
                    id = order.id,
                    name = order.name,
                    quantity = order.quantity,
                    price = order.price,
                )
            }
            SplitReceiptData(
                id = receiptWithOrders.receipt.id,
                restaurant = receiptWithOrders.receipt.restaurant,
                date = receiptWithOrders.receipt.date,
                orders = splitOrdersList,
                subTotal = receiptWithOrders.receipt.subTotal,
                total = receiptWithOrders.receipt.total,
                tax = receiptWithOrders.receipt.tax,
                discount = receiptWithOrders.receipt.discount,
                tip = receiptWithOrders.receipt.tip,
                tipSum = receiptWithOrders.receipt.tipSum,
            )
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptDataJson: ReceiptDataJson
    ): ReceiptEntity {
        return ReceiptEntity(
            restaurant = receiptDataJson.restaurant,
            date = receiptDataJson.date,
            subTotal = receiptDataJson.subTotal,
            total = receiptDataJson.total,
            tax = receiptDataJson.tax,
            discount = receiptDataJson.discount,
            tip = receiptDataJson.tip,
            tipSum = receiptDataJson.tipSum,
        )
    }

    override suspend fun transformOrderListToOrderEntity(
        orderListData: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity> {
        return orderListData.map {
            OrderEntity(
                name = it.name,
                quantity = it.quantity,
                price = it.price,
                receiptId = receiptId,
            )
        }
    }
}

interface ReceiptAdapterInterface {
    suspend fun transformReceiptWithDataListToSplitReceiptDateList(
        receiptWithOrdersList: List<ReceiptWithOrders>,
    ): List<SplitReceiptData>

    suspend fun transformReceiptDataToReceiptEntity(
        receiptDataJson: ReceiptDataJson
    ): ReceiptEntity

    suspend fun transformOrderListToOrderEntity(
        orderListData: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity>
}