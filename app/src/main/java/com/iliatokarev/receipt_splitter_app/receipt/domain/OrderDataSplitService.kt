package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_CONSUMER_NAMES
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
                if (index == position && orderDataCheck.consumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
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
        consumerName: String
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataSplit ->
                if (orderDataSplit.checked) {
                    if (consumerName in orderDataSplit.consumerNamesList) {
                        orderDataSplit.copy(checked = false)
                    }
                    else {
                        val newConsumerNamesList = orderDataSplit.consumerNamesList
                            .toMutableList()
                            .apply { add(consumerName) }
                        orderDataSplit.copy(
                            consumerNamesList = newConsumerNamesList,
                            checked = false
                        )
                    }
                }
                else
                    orderDataSplit
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataCheck ->
                if (index == position) {
                    val newConsumerNamesList = orderDataCheck.consumerNamesList.filter {
                        it != consumerName
                    }
                    orderDataCheck.copy(consumerNamesList = newConsumerNamesList, checked = false)
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
                if (orderDataCheck.consumerNamesList.isNotEmpty())
                    consumerNamesList.addAll(orderDataCheck.consumerNamesList)
            }
            return@withContext consumerNamesList.toList()
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<String>()
        }
    }

    override suspend fun clearOrderDataSplits(
        orderDataSplitList: List<OrderDataSplit>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataCheck ->
                orderDataCheck.copy(
                    consumerNamesList = emptyList(),
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
                orderDataCheck.checked
            }
        }.getOrElse { e: Throwable ->
            return@withContext false
        }
    }

    override suspend fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataCheck ->
                if (index == position)
                    orderDataCheck.copy(consumerNamesList = emptyList())
                else
                    orderDataCheck
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
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

    suspend fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ): List<OrderDataSplit>

    suspend fun getAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<String>

    suspend fun clearOrderDataSplits(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<OrderDataSplit>

    suspend fun hasExistingCheckState(
        orderDataSplitList: List<OrderDataSplit>,
    ): Boolean

    suspend fun clearAllConsumerNamesForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
    ): List<OrderDataSplit>
}