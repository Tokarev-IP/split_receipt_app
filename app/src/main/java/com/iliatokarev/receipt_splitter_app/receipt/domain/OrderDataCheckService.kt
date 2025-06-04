package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDataCheckService() : OrderDataCheckServiceInterface {

    override suspend fun convertOrderDataListToOrderDataCheckList(
        orderDataList: List<OrderData>
    ): List<OrderDataCheck> = withContext(Dispatchers.Default) {
        val orderDataCheckList = mutableListOf<OrderDataCheck>()
        for (orderData in orderDataList){
            repeat(orderData.quantity){
                orderDataCheckList.add(
                    OrderDataCheck(
                        name = orderData.name,
                        translatedName = orderData.translatedName,
                        price = orderData.price,
                    )
                )
            }
        }
        return@withContext orderDataCheckList
    }

    override suspend fun setCheckState(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int,
        state: Boolean
    ): List<OrderDataCheck> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataCheckList.mapIndexed { index, orderDataCheck ->
                if (index == position) {
                    orderDataCheck.copy(checked = state)
                } else {
                    orderDataCheck
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataCheckList
        }
    }

    override suspend fun setConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        name: String
    ): List<OrderDataCheck> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataCheckList.map { orderDataCheck ->
                if (orderDataCheck.checked && orderDataCheck.consumerName.isNullOrBlank())
                    orderDataCheck.copy(consumerName = name)
                else
                    orderDataCheck
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataCheckList
        }
    }

    override suspend fun clearConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int
    ): List<OrderDataCheck> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataCheckList.mapIndexed { index, orderDataCheck ->
                if (index == position) {
                    orderDataCheck.copy(consumerName = null, checked = false)
                } else {
                    orderDataCheck
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataCheckList
        }
    }

    override suspend fun getAllConsumerNames(
        orderDataCheckList: List<OrderDataCheck>
    ): List<String> = withContext(Dispatchers.Default) {
        runCatching {
            val consumerNamesList = mutableSetOf<String>()
            for (orderDataCheck in orderDataCheckList) {
                if (orderDataCheck.consumerName != null)
                    consumerNamesList.add(orderDataCheck.consumerName)
            }
            return@withContext consumerNamesList.toList()
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<String>()
        }
    }

    override suspend fun clearAllCheckState(
        orderDataCheckList: List<OrderDataCheck>
    ): List<OrderDataCheck> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataCheckList.map { orderDataCheck ->
                orderDataCheck.copy(
                    consumerName = null,
                    checked = false,
                )
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataCheckList
        }
    }

    override suspend fun hasExistingCheckState(
        orderDataCheckList: List<OrderDataCheck>
    ): Boolean = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataCheckList.any { orderDataCheck ->
                orderDataCheck.checked && orderDataCheck.consumerName.isNullOrEmpty()
            }
        }.getOrElse { e: Throwable ->
            return@withContext false
        }
    }
}

interface OrderDataCheckServiceInterface {
    suspend fun convertOrderDataListToOrderDataCheckList(
        orderDataList: List<OrderData>,
    ): List<OrderDataCheck>

    suspend fun setCheckState(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int,
        state: Boolean,
    ): List<OrderDataCheck>

    suspend fun setConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        name: String,
    ): List<OrderDataCheck>

    suspend fun clearConsumerName(
        orderDataCheckList: List<OrderDataCheck>,
        position: Int,
    ): List<OrderDataCheck>

    suspend fun getAllConsumerNames(
        orderDataCheckList: List<OrderDataCheck>,
    ): List<String>

    suspend fun clearAllCheckState(
        orderDataCheckList: List<OrderDataCheck>,
    ): List<OrderDataCheck>

    suspend fun hasExistingCheckState(
        orderDataCheckList: List<OrderDataCheck>,
    ): Boolean
}