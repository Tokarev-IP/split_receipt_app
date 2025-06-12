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
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position && orderDataSplit.consumerNamesList.size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES) {
                    orderDataSplit.copy(checked = state)
                } else {
                    orderDataSplit
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun addNewConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataSplit ->
                if (orderDataSplit.checked) {
                    val newConsumerNamesList = orderDataSplit.consumerNamesList
                        .toMutableSet()
                        .apply { addAll(consumerNamesList) }
                        .toList()
                    orderDataSplit.copy(
                        consumerNamesList = newConsumerNamesList,
                        checked = false
                    )
                } else
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
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position) {
                    val newConsumerNamesList = orderDataSplit.consumerNamesList.filter {
                        it != consumerName
                    }
                    orderDataSplit.copy(consumerNamesList = newConsumerNamesList, checked = false)
                } else {
                    orderDataSplit
                }
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun getAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
        initialConsumerNamesList: List<String>,
    ): List<String> = withContext(Dispatchers.Default) {
        runCatching {
            val consumerNamesSet = mutableSetOf<String>()
            consumerNamesSet.addAll(initialConsumerNamesList)
            for (orderDataCheck in orderDataSplitList) {
                if (orderDataCheck.consumerNamesList.isNotEmpty())
                    consumerNamesSet.addAll(orderDataCheck.consumerNamesList)
            }
            val consumerNamesList = consumerNamesSet.toList()
            return@withContext consumerNamesList
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<String>()
        }
    }

    override suspend fun clearOrderDataSplits(
        orderDataSplitList: List<OrderDataSplit>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.map { orderDataSplit ->
                orderDataSplit.copy(
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
            return@withContext orderDataSplitList.any { orderDataSplit ->
                orderDataSplit.checked
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
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position)
                    orderDataSplit.copy(consumerNamesList = emptyList())
                else
                    orderDataSplit
            }
        }.getOrElse { e: Throwable ->
            return@withContext orderDataSplitList
        }
    }

    override suspend fun addNewConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            return@withContext orderDataSplitList.mapIndexed { index, orderDataSplit ->
                if (index == position) {
                    val newConsumerNameList = orderDataSplit.consumerNamesList
                        .toMutableList()
                        .apply {
                            if (size < MAXIMUM_AMOUNT_OF_CONSUMER_NAMES)
                                add(name)
                        }
                    orderDataSplit.copy(consumerNamesList = newConsumerNameList)
                } else
                    orderDataSplit
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

    suspend fun addNewConsumerNamesForCheckedOrders(
        orderDataSplitList: List<OrderDataSplit>,
        consumerNamesList: List<String>,
    ): List<OrderDataSplit>

    suspend fun clearSpecificConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        consumerName: String,
    ): List<OrderDataSplit>

    suspend fun getAllConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
        initialConsumerNamesList: List<String>,
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

    suspend fun addNewConsumerNameForSpecificOrder(
        orderDataSplitList: List<OrderDataSplit>,
        position: Int,
        name: String,
    ): List<OrderDataSplit>
}