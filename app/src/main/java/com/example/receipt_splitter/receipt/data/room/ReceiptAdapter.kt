package com.example.receipt_splitter.receipt.data.room

import com.example.receipt_splitter.main.basic.roundToTwoDecimalPlaces
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.OrderDataJson
import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson

class ReceiptAdapter : ReceiptAdapterInterface {

    override suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData> {
        return receiptEntityList.map { receiptEntity ->
            ReceiptData(
                id = receiptEntity.id,
                receiptName = receiptEntity.restaurant,
                translatedReceiptName = receiptEntity.translatedRestaurant,
                date = receiptEntity.date,
                total = receiptEntity.total.roundToTwoDecimalPlaces(),
                tax = receiptEntity.tax?.roundToTwoDecimalPlaces(),
                discount = receiptEntity.discount?.roundToTwoDecimalPlaces(),
                tip = receiptEntity.tip?.roundToTwoDecimalPlaces(),
            )
        }
    }

    override suspend fun transformReceiptEntityToReceiptDate(
        receiptEntity: ReceiptEntity
    ): ReceiptData {
        return receiptEntity.run {
            ReceiptData(
                id = id,
                receiptName = restaurant,
                translatedReceiptName = translatedRestaurant,
                date = date,
                total = total.roundToTwoDecimalPlaces(),
                tax = tax?.roundToTwoDecimalPlaces(),
                discount = discount?.roundToTwoDecimalPlaces(),
                tip = tip?.roundToTwoDecimalPlaces(),
            )
        }
    }

    override suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson
    ): ReceiptEntity {
        return ReceiptEntity(
            restaurant = receiptDataJson.receiptName,
            translatedRestaurant = receiptDataJson.translatedReceiptName,
            date = receiptDataJson.date,
            total = receiptDataJson.total.roundToTwoDecimalPlaces(),
            tax = receiptDataJson.tax?.roundToTwoDecimalPlaces(),
            discount = receiptDataJson.discount?.roundToTwoDecimalPlaces(),
            tip = receiptDataJson.tip?.roundToTwoDecimalPlaces(),
            tipSum = receiptDataJson.tipSum?.roundToTwoDecimalPlaces(),
        )
    }

    override suspend fun transformOrderDataJsonListToOrderEntity(
        orderDataJsonList: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity> {
        return orderDataJsonList.map { orderDataJson ->
            OrderEntity(
                name = orderDataJson.name,
                translatedName = orderDataJson.translatedName,
                quantity = orderDataJson.quantity,
                price = orderDataJson.price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
            )
        }
    }

    override suspend fun transformOrderEntityListToOrderDataList(
        orderEntityList: List<OrderEntity>
    ): List<OrderData> {
        return orderEntityList.map { orderEntity ->
            OrderData(
                id = orderEntity.id,
                name = orderEntity.name,
                translatedName = orderEntity.translatedName,
                quantity = orderEntity.quantity,
                price = orderEntity.price.roundToTwoDecimalPlaces(),
                receiptId = orderEntity.receiptId,
            )
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity {
        return ReceiptEntity(
            id = receiptData.id,
            restaurant = receiptData.receiptName,
            translatedRestaurant = receiptData.translatedReceiptName,
            date = receiptData.date,
            total = receiptData.total.roundToTwoDecimalPlaces(),
            tax = receiptData.tax?.roundToTwoDecimalPlaces(),
            discount = receiptData.discount?.roundToTwoDecimalPlaces(),
            tip = receiptData.tip?.roundToTwoDecimalPlaces(),
        )
    }

    override suspend fun transformOrderDataToOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                id = id,
                name = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
            )
        }
    }

    override suspend fun transformOrderDataToNewOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                name = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
            )
        }
    }
}

interface ReceiptAdapterInterface {
    suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData>

    suspend fun transformReceiptEntityToReceiptDate(
        receiptEntity: ReceiptEntity,
    ): ReceiptData

    suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson
    ): ReceiptEntity

    suspend fun transformOrderDataJsonListToOrderEntity(
        orderListData: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity>

    suspend fun transformOrderEntityListToOrderDataList(
        orderEntityList: List<OrderEntity>,
    ): List<OrderData>

    suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData,
    ): ReceiptEntity

    suspend fun transformOrderDataToOrderEntity(
        orderData: OrderData,
    ): OrderEntity

    suspend fun transformOrderDataToNewOrderEntity(
        orderData: OrderData
    ): OrderEntity
}