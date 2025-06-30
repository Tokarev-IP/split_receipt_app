package com.iliatokarev.receipt_splitter_app.receipt.domain.reports

import com.iliatokarev.receipt_splitter_app.main.basic.isMoreThanOne
import com.iliatokarev.receipt_splitter_app.main.basic.isPositive
import com.iliatokarev.receipt_splitter_app.main.basic.isNotZero
import com.iliatokarev.receipt_splitter_app.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReportCreator() : OrderReportCreatorInterface {

    private companion object {
        const val EMPTY_STRING = ""
        const val START_STRING = "·"
        const val LONG_DIVIDER_STRING = "---------------"
        const val SHORT_DIVIDER_STRING = "------"
        const val EQUAL_STRING = "="
    }

    override suspend fun buildOrderReportForOne(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): String? {
        return withContext(Dispatchers.Default) {
            runCatching {
                val orderReport = StringBuilder()
                var finalPrice = 0f
                var index = 1

                if (receiptData.receiptName.isNotEmpty())
                    orderReport.append("${receiptData.receiptName}\n")
                if (receiptData.date.isNotEmpty())
                    orderReport.append("${receiptData.date}\n")

                if (orderDataList.isNotEmpty())
                    orderReport.append("$SHORT_DIVIDER_STRING\n")

                for (orderData in orderDataList) {
                    if (orderData.selectedQuantity.isPositive()) {
                        val sumPrice = orderData.selectedQuantity * orderData.price
                        finalPrice += sumPrice
                        if (orderData.selectedQuantity.isMoreThanOne())
                            orderReport.append(" $index. ${orderData.name} ${orderData.translatedName ?: EMPTY_STRING} $EQUAL_STRING ${orderData.selectedQuantity} x ${orderData.price.roundToTwoDecimalPlaces()} $EQUAL_STRING ${sumPrice.roundToTwoDecimalPlaces()}\n")
                        else
                            orderReport.append(" $index. ${orderData.name} ${orderData.translatedName ?: EMPTY_STRING} $EQUAL_STRING ${sumPrice.roundToTwoDecimalPlaces()}\n")
                        index++
                    }
                }

                if ((receiptData.discount != null
                            || receiptData.tip != null
                            || receiptData.tax != null)
                    && finalPrice.isNotZero()
                ) {
                    orderReport.append(" $EQUAL_STRING ${finalPrice.roundToTwoDecimalPlaces()}")

                    receiptData.discount?.let { discount ->
                        finalPrice -= (finalPrice * discount) / 100
                        orderReport.append(" - ${discount.roundToTwoDecimalPlaces()} %")
                    }
                    receiptData.tip?.let { tip ->
                        finalPrice += (finalPrice * tip) / 100
                        orderReport.append(" + ${tip.roundToTwoDecimalPlaces()} %")
                    }
                    receiptData.tax?.let { tax ->
                        finalPrice += (finalPrice * tax) / 100
                        orderReport.append(" + ${tax.roundToTwoDecimalPlaces()} %")
                    }
                    orderReport.append(" $EQUAL_STRING ${finalPrice.roundToTwoDecimalPlaces()}\n")
                }

                orderReport.append("$EQUAL_STRING ${finalPrice.roundToTwoDecimalPlaces()}")

                return@withContext orderReport.toString().trim()
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }

    override suspend fun buildOrderReportForAll(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
    ): String? {
        return withContext(Dispatchers.Default) {
            runCatching {
                val consumerNameList = extractConsumerNames(orderDataSplitList)
                if (consumerNameList.isEmpty())
                    return@withContext null

                val orderReport = StringBuilder()

                if (receiptData.receiptName.isNotEmpty())
                    orderReport.append("${receiptData.receiptName}\n")
                if (receiptData.date.isNotEmpty())
                    orderReport.append("${receiptData.date}\n")

                if (consumerNameList.isNotEmpty())
                    orderReport.append("$SHORT_DIVIDER_STRING\n")

                for (consumerName in consumerNameList) {
                    var index = 1
                    var consumerFinalPrice = 0F
                    val newOrderDataSplitList =
                        orderDataSplitList.filter { consumerName in it.consumerNamesList }

                    orderReport.append("$START_STRING ${consumerName}\n")

                    for (orderDataSplit in newOrderDataSplitList) {
                        if (orderDataSplit.consumerNamesList.size.isMoreThanOne()) {
                            val newPrice =
                                (orderDataSplit.price / orderDataSplit.consumerNamesList.size).roundToTwoDecimalPlaces()
                            orderReport.append("  $index. ${orderDataSplit.name} ${orderDataSplit.translatedName ?: EMPTY_STRING} $EQUAL_STRING 1/${orderDataSplit.consumerNamesList.size} x ${orderDataSplit.price.roundToTwoDecimalPlaces()} $EQUAL_STRING ${newPrice.roundToTwoDecimalPlaces()}\n")
                            consumerFinalPrice += newPrice
                        } else {
                            orderReport.append("  $index. ${orderDataSplit.name} ${orderDataSplit.translatedName ?: EMPTY_STRING} $EQUAL_STRING ${orderDataSplit.price.roundToTwoDecimalPlaces()}\n")
                            consumerFinalPrice += orderDataSplit.price.roundToTwoDecimalPlaces()
                        }
                        index++
                    }

                    if ((receiptData.discount != null
                                || receiptData.tip != null
                                || receiptData.tax != null)
                        && consumerFinalPrice.isNotZero()
                    ) {
                        orderReport.append("  $EQUAL_STRING ${consumerFinalPrice.roundToTwoDecimalPlaces()}")

                        receiptData.discount?.let { discount ->
                            consumerFinalPrice -= (consumerFinalPrice * discount) / 100
                            orderReport.append(" - ${discount.roundToTwoDecimalPlaces()} %")
                        }
                        receiptData.tip?.let { tip ->
                            consumerFinalPrice += (consumerFinalPrice * tip) / 100
                            orderReport.append(" + ${tip.roundToTwoDecimalPlaces()} %")
                        }
                        receiptData.tax?.let { tax ->
                            consumerFinalPrice += (consumerFinalPrice * tax) / 100
                            orderReport.append(" + ${tax.roundToTwoDecimalPlaces()} %")
                        }
                        orderReport.append(" $EQUAL_STRING ${consumerFinalPrice.roundToTwoDecimalPlaces()}\n")
                    }
                    orderReport.append("$EQUAL_STRING ${consumerFinalPrice.roundToTwoDecimalPlaces()}\n")
                    orderReport.append("$LONG_DIVIDER_STRING\n")
                }
                return@withContext orderReport.toString().trim()
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }

    private fun extractConsumerNames(
        orderDataSplitList: List<OrderDataSplit>,
    ): List<String> {
        val consumerNamesSet = mutableSetOf<String>()
        for (orderDataSplit in orderDataSplitList) {
            consumerNamesSet.addAll(orderDataSplit.consumerNamesList)
        }
        return consumerNamesSet.toList()
    }
}


interface OrderReportCreatorInterface {
    suspend fun buildOrderReportForOne(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): String?

    suspend fun buildOrderReportForAll(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
    ): String?
}