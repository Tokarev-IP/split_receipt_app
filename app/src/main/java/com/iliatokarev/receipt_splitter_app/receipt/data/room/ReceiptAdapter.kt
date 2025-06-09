package com.iliatokarev.receipt_splitter_app.receipt.data.room

import com.iliatokarev.receipt_splitter_app.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.RECEIPT_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataJson
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptDataJson

class ReceiptAdapter : ReceiptAdapterInterface {

    override suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData> {
        return receiptEntityList.map { receiptEntity ->
            ReceiptData(
                id = receiptEntity.id,
                receiptName = receiptEntity.receiptName,
                translatedReceiptName = receiptEntity.translatedReceiptName,
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
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
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
            receiptName = receiptDataJson.receiptName,
            translatedReceiptName = receiptDataJson.translatedReceiptName,
            date = receiptDataJson.date,
            total = receiptDataJson.total.roundToTwoDecimalPlaces(),
            tax = receiptDataJson.tax?.roundToTwoDecimalPlaces(),
            discount = receiptDataJson.discount?.roundToTwoDecimalPlaces(),
            tip = receiptDataJson.tip?.roundToTwoDecimalPlaces(),
        )
    }

    override suspend fun transformOrderDataJsonListToOrderEntityList(
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
                consumersList = orderEntity.consumerNames.split(RECEIPT_CONSUMER_NAME_DIVIDER),
            )
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity {
        return ReceiptEntity(
            id = receiptData.id,
            receiptName = receiptData.receiptName,
            translatedReceiptName = receiptData.translatedReceiptName,
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
                consumerNames = consumersList.joinToString(RECEIPT_CONSUMER_NAME_DIVIDER)
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
                consumerNames = consumersList.joinToString(RECEIPT_CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformOrderDataListToOrderEntityList(
        orderDataList: List<OrderData>
    ): List<OrderEntity> {
        return orderDataList.map { orderData ->
            OrderEntity(
                id = orderData.id,
                name = orderData.name,
                translatedName = orderData.translatedName,
                quantity = orderData.quantity,
                price = orderData.price.roundToTwoDecimalPlaces(),
                receiptId = orderData.receiptId,
                consumerNames = orderData.consumersList.joinToString(RECEIPT_CONSUMER_NAME_DIVIDER)
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

    suspend fun transformOrderDataJsonListToOrderEntityList(
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

    suspend fun transformOrderDataListToOrderEntityList(
        orderDataList: List<OrderData>
    ): List<OrderEntity>
}