package com.iliatokarev.receipt_splitter_app.receipt.domain.reports

import com.iliatokarev.receipt_splitter_app.main.basic.isMoreThanOne
import com.iliatokarev.receipt_splitter_app.main.basic.isNotZero
import com.iliatokarev.receipt_splitter_app.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptWithOrdersDataSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderReceiptsReportCreator() : FolderReceiptsReportCreatorInterface {

    private companion object {
        const val EMPTY_STRING = ""
        const val START_STRING = "·"
        const val LONG_DIVIDER_STRING = "---------------"
        const val SHORT_DIVIDER_STRING = "------"
        const val EQUAL_STRING = "="
    }

    override suspend fun createBasicReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String? = withContext(Dispatchers.Default) {
        runCatching {
            val report = StringBuilder()
            var index = 1

            for (consumer in consumerNamesList) {
                var consumerTotalSum = 0F

                report.append("$index. ${consumer}\n")

                for (receiptWithOrdersDataSplit in receiptWithOrdersDataSplitList) {
                    val isContained = isConsumerNameInOrders(
                        consumerName = consumer,
                        ordersList = receiptWithOrdersDataSplit.orders,
                    )
                    if (isContained) {
                        var receiptTotalSum = 0F

                        for (order in receiptWithOrdersDataSplit.orders) {
                            if (consumer in order.consumerNamesList) {
                                val orderPrice =
                                    (order.price / order.consumerNamesList.size).roundToTwoDecimalPlaces()
                                receiptTotalSum += orderPrice
                            }
                        }

                        receiptWithOrdersDataSplit.receipt.discount?.let { discount ->
                            receiptTotalSum -= (receiptTotalSum * discount) / 100
                        }
                        receiptWithOrdersDataSplit.receipt.tip?.let { tip ->
                            receiptTotalSum += (receiptTotalSum * tip) / 100
                        }
                        receiptWithOrdersDataSplit.receipt.tax?.let { tax ->
                            receiptTotalSum += (receiptTotalSum * tax) / 100
                        }

                        report.append("  $START_STRING ${receiptWithOrdersDataSplit.receipt.receiptName} ${receiptWithOrdersDataSplit.receipt.translatedReceiptName ?: EMPTY_STRING} $EQUAL_STRING ${receiptTotalSum.roundToTwoDecimalPlaces()}\n")
                        consumerTotalSum += receiptTotalSum
                    }
                }
                report.append("$EQUAL_STRING ${consumerTotalSum.roundToTwoDecimalPlaces()}\n")
                report.append("$LONG_DIVIDER_STRING\n")
                index++
            }
            return@withContext report.toString().trim()
        }.getOrElse { e: Throwable ->
            return@withContext null
        }
    }

    override suspend fun createShortReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String? = withContext(Dispatchers.Default) {
        runCatching {
            val report = StringBuilder()
            var index = 1

            for (receipt in receiptWithOrdersDataSplitList){
                report.append("$START_STRING ${receipt.receipt.receiptName} ${receipt.receipt.translatedReceiptName ?: EMPTY_STRING}\n")
            }

            report.append("$SHORT_DIVIDER_STRING\n")

            for (consumer in consumerNamesList) {
                var consumerTotalSum = 0F

                for (receiptWithOrdersDataSplit in receiptWithOrdersDataSplitList) {
                    val isContained = isConsumerNameInOrders(
                        consumerName = consumer,
                        ordersList = receiptWithOrdersDataSplit.orders,
                    )
                    if (isContained) {
                        var receiptTotalSum = 0F

                        for (order in receiptWithOrdersDataSplit.orders) {
                            if (consumer in order.consumerNamesList) {
                                val orderPrice =
                                    (order.price / order.consumerNamesList.size).roundToTwoDecimalPlaces()
                                receiptTotalSum += orderPrice
                            }
                        }
                        receiptWithOrdersDataSplit.receipt.discount?.let { discount ->
                            receiptTotalSum -= (receiptTotalSum * discount) / 100
                        }
                        receiptWithOrdersDataSplit.receipt.tip?.let { tip ->
                            receiptTotalSum += (receiptTotalSum * tip) / 100
                        }
                        receiptWithOrdersDataSplit.receipt.tax?.let { tax ->
                            receiptTotalSum += (receiptTotalSum * tax) / 100
                        }

                        consumerTotalSum += receiptTotalSum
                    }
                }
                report.append("$index. $consumer $EQUAL_STRING ${consumerTotalSum.roundToTwoDecimalPlaces()}\n")
                index++
            }
            return@withContext report.toString().trim()
        }.getOrElse { e: Throwable ->
            return@withContext null
        }
    }

    override suspend fun createLongReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String? = withContext(Dispatchers.Default) {
        runCatching {
            val report = StringBuilder()

            for (consumer in consumerNamesList) {
                var consumerTotalSum = 0F

                report.append("$consumer\n")

                for (receiptWithOrdersDataSplit in receiptWithOrdersDataSplitList) {
                    val isContained = isConsumerNameInOrders(
                        consumerName = consumer,
                        ordersList = receiptWithOrdersDataSplit.orders,
                    )
                    var index = 1
                    if (isContained) {
                        var receiptTotalSum = 0F

                        report.append("   $SHORT_DIVIDER_STRING\n")
                        report.append(" $START_STRING ${receiptWithOrdersDataSplit.receipt.receiptName} ${receiptWithOrdersDataSplit.receipt.translatedReceiptName ?: EMPTY_STRING}\n")

                        for (orderDataSplit in receiptWithOrdersDataSplit.orders) {
                            if (consumer in orderDataSplit.consumerNamesList) {
                                val orderPrice =
                                    (orderDataSplit.price / orderDataSplit.consumerNamesList.size).roundToTwoDecimalPlaces()
                                if (orderDataSplit.consumerNamesList.size.isMoreThanOne()) {
                                    report.append("   $index. ${orderDataSplit.name} ${orderDataSplit.translatedName ?: EMPTY_STRING} $EQUAL_STRING 1/${orderDataSplit.consumerNamesList.size} x ${orderDataSplit.price} $EQUAL_STRING ${orderPrice.roundToTwoDecimalPlaces()}\n")
                                    receiptTotalSum += orderPrice
                                } else {
                                    report.append("   $index. ${orderDataSplit.name} ${orderDataSplit.translatedName ?: EMPTY_STRING} $EQUAL_STRING ${orderDataSplit.price.roundToTwoDecimalPlaces()}\n")
                                    receiptTotalSum += orderDataSplit.price
                                }
                                index++
                            }
                        }

                        if ((receiptWithOrdersDataSplit.receipt.discount != null
                            || receiptWithOrdersDataSplit.receipt.tip != null
                            || receiptWithOrdersDataSplit.receipt.tax != null)
                            && receiptTotalSum.isNotZero()
                        ) {
                            report.append("   $EQUAL_STRING ${receiptTotalSum.roundToTwoDecimalPlaces()}")

                            receiptWithOrdersDataSplit.receipt.discount?.let { discount ->
                                receiptTotalSum -= (receiptTotalSum * discount) / 100
                                report.append(" - ${discount.roundToTwoDecimalPlaces()} %")
                            }
                            receiptWithOrdersDataSplit.receipt.tip?.let { tip ->
                                receiptTotalSum += (receiptTotalSum * tip) / 100
                                report.append(" + ${tip.roundToTwoDecimalPlaces()} %")
                            }
                            receiptWithOrdersDataSplit.receipt.tax?.let { tax ->
                                receiptTotalSum += (receiptTotalSum * tax) / 100
                                report.append(" + ${tax.roundToTwoDecimalPlaces()} %")
                            }
                            report.append(" $EQUAL_STRING ${receiptTotalSum.roundToTwoDecimalPlaces()}\n")
                        } else
                            report.append("   $EQUAL_STRING ${receiptTotalSum.roundToTwoDecimalPlaces()}\n")
                        consumerTotalSum += receiptTotalSum
                    }
                }
                report.append("   $SHORT_DIVIDER_STRING\n")
                report.append("$EQUAL_STRING ${consumerTotalSum.roundToTwoDecimalPlaces()}\n")
                report.append("$LONG_DIVIDER_STRING\n")
            }
            return@withContext report.toString().trim()
        }.getOrElse { e: Throwable ->
            return@withContext null
        }
    }


    private fun isConsumerNameInOrders(
        consumerName: String,
        ordersList: List<OrderDataSplit>,
    ): Boolean {
        return ordersList.any { orderDataSplit ->
            consumerName in orderDataSplit.consumerNamesList
        }
    }
}

interface FolderReceiptsReportCreatorInterface {
    suspend fun createBasicReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String?

    suspend fun createShortReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String?

    suspend fun createLongReport(
        consumerNamesList: List<String>,
        receiptWithOrdersDataSplitList: List<ReceiptWithOrdersDataSplit>,
    ): String?
}