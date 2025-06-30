package com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt

import com.iliatokarev.receipt_splitter_app.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter_app.receipt.data.room.OrderEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.CONSUMER_NAME_DIVIDER
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
                folderId = receiptEntity.folderId,
                isShared = receiptEntity.isShared,
            )
        }
    }

    override suspend fun transformReceiptEntityToReceiptData(
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
                folderId = folderId,
                isShared = isShared
            )
        }
    }

    override suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson,
        folderId: Long?,
    ): ReceiptEntity {
        return receiptDataJson.run {
            ReceiptEntity(
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
                date = date,
                total = total.roundToTwoDecimalPlaces(),
                tax = tax?.roundToTwoDecimalPlaces(),
                discount = discount?.roundToTwoDecimalPlaces(),
                tip = tip?.roundToTwoDecimalPlaces(),
                folderId = folderId,
            )
        }
    }

    override suspend fun transformOrderDataJsonListToOrderEntityList(
        orderDataJsonList: List<OrderDataJson>,
        receiptId: Long,
    ): List<OrderEntity> {
        return orderDataJsonList.map { orderDataJson ->
            OrderEntity(
                orderName = orderDataJson.name,
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
                name = orderEntity.orderName,
                translatedName = orderEntity.translatedName,
                quantity = orderEntity.quantity,
                price = orderEntity.price.roundToTwoDecimalPlaces(),
                receiptId = orderEntity.receiptId,
                consumersList = orderEntity.consumerNames
                    .split(CONSUMER_NAME_DIVIDER)
                    .filter { it.isNotEmpty() },
            )
        }
    }

    override suspend fun transformReceiptDataToReceiptEntity(
        receiptData: ReceiptData
    ): ReceiptEntity {
        return receiptData.run {
            ReceiptEntity(
                id = id,
                receiptName = receiptName,
                translatedReceiptName = translatedReceiptName,
                date = date,
                total = total.roundToTwoDecimalPlaces(),
                tax = tax?.roundToTwoDecimalPlaces(),
                discount = discount?.roundToTwoDecimalPlaces(),
                tip = tip?.roundToTwoDecimalPlaces(),
                folderId = folderId,
                isShared = isShared,
            )
        }
    }

    override suspend fun transformOrderDataToOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                id = id,
                orderName = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
                consumerNames = consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformOrderDataToNewOrderEntity(orderData: OrderData): OrderEntity {
        return orderData.run {
            OrderEntity(
                orderName = name,
                translatedName = translatedName,
                quantity = quantity,
                price = price.roundToTwoDecimalPlaces(),
                receiptId = receiptId,
                consumerNames = consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }

    override suspend fun transformOrderDataListToOrderEntityList(
        orderDataList: List<OrderData>
    ): List<OrderEntity> {
        return orderDataList.map { orderData ->
            OrderEntity(
                id = orderData.id,
                orderName = orderData.name,
                translatedName = orderData.translatedName,
                quantity = orderData.quantity,
                price = orderData.price.roundToTwoDecimalPlaces(),
                receiptId = orderData.receiptId,
                consumerNames = orderData.consumersList.joinToString(CONSUMER_NAME_DIVIDER)
            )
        }
    }
}

interface ReceiptAdapterInterface {
    suspend fun transformReceiptEntityListToReceiptDateList(
        receiptEntityList: List<ReceiptEntity>,
    ): List<ReceiptData>

    suspend fun transformReceiptEntityToReceiptData(
        receiptEntity: ReceiptEntity,
    ): ReceiptData

    suspend fun transformReceiptDataJsonToReceiptEntity(
        receiptDataJson: ReceiptDataJson,
        folderId: Long?,
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