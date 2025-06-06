package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderDataSplitService() : OrderDataSplitServiceInterface {

    override suspend fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataCheck ->
                if (index == position) {
                    orderDataCheck.copy(checked = state)
                } else {
                    orderDataCheck
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun setConsumerName(
        orderDataSplitList: List<OrderDataSplit>,
        name: String
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataCheck ->
                if (orderDataCheck.checked && orderDataCheck.consumerName.isNullOrBlank())
                    orderDataCheck.copy(consumerName = name)
                else
                    orderDataCheck
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun clearConsumerName(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataCheck ->
                if (index == position) {
                    orderDataCheck.copy(consumerName = null, checked = false)
                } else {
                    orderDataCheck
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun getAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>
    ): List<String> = withContext(Dispatchers.Default) {
        runCatching {
            val consumerNamesList = mutableSetOf<String>()
            for (orderDataCheck in orderDataSplitList) {
                if (orderDataCheck.consumerName != null)
                    consumerNamesList.add(orderDataCheck.consumerName)
            }
            return@withContext consumerNamesList.toList()
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<String>()
        }
    }

    override suspend fun clearAllCheckState(
        orderDataSplitList: List<OrderDataSplit>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataCheck ->
                orderDataCheck.copy(
                    consumerName = null,
                    checked = false,
                )
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun hasExistingCheckState(
        orderDataSplitList: List<OrderDataSplit>
    ): Boolean = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.any { orderDataCheck ->
                orderDataCheck.checked && orderDataCheck.consumerName.isNullOrEmpty()
            }
        }.getOrElse { e: Throwable ->
            return@withContext false
        }
    }
}

interface OrderDataSplitServiceInterface {
    suspend fun setCheckState(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        state: Boolean,
    ): List<OrderDataSplit>

    suspend fun setConsumerName(
        orderDataSplitList: List<OrderDataSplit>,
        name: String,
    ): List<OrderDataSplit>

    suspend fun clearConsumerName(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
    ): List<OrderDataSplit>

    suspend fun getAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<String>

    suspend fun clearAllCheckState(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<OrderDataSplit>

    suspend fun hasExistingCheckState(
        orderDataSplitList: List<OrderDataSplit>,
    ): Boolean
}