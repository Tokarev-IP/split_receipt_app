package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ORDER_CONSUMER_NAME_DIVIDER
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplitReceiptUseCase(
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : SplitReceiptUseCaseInterface {

    override suspend fun retrieveReceiptData(receiptId: Long): ReceiptData? {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getReceiptDataById(id = receiptId)
            }.getOrNull()
        }
    }

    override suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData> {
        return withContext(Dispatchers.IO) {
            runCatching {
                return@withContext receiptDbRepository.getOrdersByReceiptId(receiptId = receiptId)
            }.getOrElse {
                return@withContext emptyList()
            }
        }
    }

    override suspend fun convertOrderDataListToOrderDataSplitList(
        orderDataList: List<OrderData>
    ): List<OrderDataSplit> = withContext(Dispatchers.Default) {
        runCatching {
            val orderDataSplitList = mutableListOf<OrderDataSplit>()
            for (orderData in orderDataList) {
                val consumerNames = orderData.consumersList
                repeat(orderData.quantity) { index ->
                    var consumerName: String? = null
                    if (consumerNames.size > index) {
                        consumerName =
                            if (consumerNames[index].isEmpty() == true) null else consumerNames[index]
                    }
                    orderDataSplitList.add(
                        OrderDataSplit(
                            name = orderData.name,
                            translatedName = orderData.translatedName,
                            price = orderData.price,
                            orderDataId = orderData.id,
                            consumerNamesList = consumerName?.split(ORDER_CONSUMER_NAME_DIVIDER)
                                ?: emptyList(),
                            checked = false,
                        )
                    )
                }
            }
            return@withContext orderDataSplitList
        }.getOrElse { e: Throwable ->
            return@withContext emptyList<OrderDataSplit>()
        }
    }

    override suspend fun saveOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>
    ): BasicFunResponse = withContext(Dispatchers.IO) {
        runCatching {
            val newOrderDataList = orderDataList.map { orderData ->
                transformOrderDataSplitListToOrderData(
                    orderDataSplitList = orderDataSplitList.filter { it.orderDataId == orderData.id },
                    orderData = orderData
                )
            }
            receiptDbRepository.insertOrderDataLists(orderDataList = newOrderDataList)
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    private fun transformOrderDataSplitListToOrderData(
        orderDataSplitList: List<OrderDataSplit>,
        orderData: OrderData,
    ): OrderData {
        val newConsumerList = mutableListOf<String>()
        for (orderDataSplit in orderDataSplitList) {
            val consumerNames = orderDataSplit.consumerNamesList.joinToString(ORDER_CONSUMER_NAME_DIVIDER)
            if (consumerNames.isNotEmpty())
                newConsumerList.add(consumerNames)
        }
        return orderData.copy(consumersList = newConsumerList)
    }

}

interface SplitReceiptUseCaseInterface {
    suspend fun retrieveReceiptData(receiptId: Long): ReceiptData?
    suspend fun retrieveOrderDataList(receiptId: Long): List<OrderData>
    suspend fun convertOrderDataListToOrderDataSplitList(orderDataList: List<OrderData>): List<OrderDataSplit>
    suspend fun saveOrderDataSplitList(
        orderDataSplitList: List<OrderDataSplit>,
        orderDataList: List<OrderData>
    ): BasicFunResponse
}