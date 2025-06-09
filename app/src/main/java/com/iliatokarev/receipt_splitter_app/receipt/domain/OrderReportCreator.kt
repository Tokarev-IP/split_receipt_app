package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.main.basic.isMoreThanOne
import com.iliatokarev.receipt_splitter_app.main.basic.isNotZero
import com.iliatokarev.receipt_splitter_app.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderDataSplit
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReportCreator() : OrderReportCreatorInterface {

    override suspend fun buildOrderReportForOne(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): String? {
        return withContext(Dispatchers.Default) {
            runCatching {
                val orderReport = StringBuilder()
                var finalPrice = 0f

                if (receiptData.receiptName.isNotEmpty())
                    orderReport.append("${receiptData.receiptName} \n")
                if (receiptData.date.isNotEmpty())
                    orderReport.append("${receiptData.date} \n")

                if (orderDataList.isNotEmpty())
                    orderReport.append("--------------\n")

                for (splitReceiptData in orderDataList) {
                    if (splitReceiptData.selectedQuantity.isNotZero()) {
                        val sumPrice = splitReceiptData.selectedQuantity * splitReceiptData.price
                        finalPrice += sumPrice
                        orderReport.append("· ${splitReceiptData.name} ${splitReceiptData.translatedName ?: ""}   ${splitReceiptData.selectedQuantity} x ${splitReceiptData.price.roundToTwoDecimalPlaces()}  =  ${sumPrice.roundToTwoDecimalPlaces()}\n")
                    }
                }

                if (receiptData.discount != null
                    || receiptData.tip != null
                    || receiptData.tax != null
                ) {
                    orderReport.append("= ${finalPrice.roundToTwoDecimalPlaces()}\n")

                    receiptData.discount?.let { discount ->
                        finalPrice -= (finalPrice * discount) / 100
                        orderReport.append(" - $discount % \n")
                    }
                    receiptData.tip?.let { tip ->
                        finalPrice += (finalPrice * tip) / 100
                        orderReport.append(" + $tip % \n")
                    }
                    receiptData.tax?.let { tax ->
                        finalPrice += (finalPrice * tax) / 100
                        orderReport.append(" + $tax % \n")
                    }
                    orderReport.append("= ${finalPrice.roundToTwoDecimalPlaces()}\n")
                }

                if (receiptData.additionalSumList.isNotEmpty()) {
                    orderReport.append("------\n")
                    for (additionalSum in receiptData.additionalSumList) {
                        orderReport.append("${additionalSum.first}     ${additionalSum.second}\n")
                        finalPrice += additionalSum.second
                    }
                    orderReport.append("= ${finalPrice.roundToTwoDecimalPlaces()}\n")
                }

                if (finalPrice.isNotZero())
                    orderReport.append("--------------\n")

                orderReport.append("${finalPrice.roundToTwoDecimalPlaces()}")

                return@withContext orderReport.toString()
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }

    override suspend fun buildOrderReportForAll(
        receiptData: ReceiptData,
        orderDataSplitList: List<OrderDataSplit>,
        consumerNameList: List<String>,
    ): String? {
        return withContext(Dispatchers.Default) {
            runCatching {
                if (consumerNameList.isEmpty())
                    return@withContext null

                val orderReport = StringBuilder()

                if (receiptData.receiptName.isNotEmpty())
                    orderReport.append("${receiptData.receiptName} \n")
                if (receiptData.date.isNotEmpty())
                    orderReport.append("${receiptData.date} \n")

                if (consumerNameList.isNotEmpty())
                    orderReport.append("--------------\n")
                for (consumerName in consumerNameList) {
                    var consumerFinalPrice = 0F
                    val newOrderDataSplitList =
                        orderDataSplitList.filter { consumerName in it.consumerNamesList }

                    orderReport.append("${consumerName}\n")

                    for (orderDataSplit in newOrderDataSplitList) {
                        if (orderDataSplit.consumerNamesList.size.isMoreThanOne()) {
                            val newPrice = (orderDataSplit.price / orderDataSplit.consumerNamesList.size).roundToTwoDecimalPlaces()
                            orderReport.append("· ${orderDataSplit.name} ${orderDataSplit.translatedName ?: ""} 1/${orderDataSplit.consumerNamesList.size} x ${orderDataSplit.price} = ${newPrice}\n")
                            consumerFinalPrice += newPrice
                        } else {
                            orderReport.append("· ${orderDataSplit.name} ${orderDataSplit.translatedName ?: ""}   ${orderDataSplit.price}\n")
                            consumerFinalPrice += orderDataSplit.price.roundToTwoDecimalPlaces()
                        }
                    }

                    if (receiptData.discount != null
                        || receiptData.tip != null
                        || receiptData.tax != null
                    ) {
                        orderReport.append("= ${consumerFinalPrice.roundToTwoDecimalPlaces()}\n")

                        receiptData.discount?.let { discount ->
                            consumerFinalPrice -= (consumerFinalPrice * discount) / 100
                            orderReport.append(" - $discount % \n")
                        }
                        receiptData.tip?.let { tip ->
                            consumerFinalPrice += (consumerFinalPrice * tip) / 100
                            orderReport.append(" + $tip % \n")
                        }
                        receiptData.tax?.let { tax ->
                            consumerFinalPrice += (consumerFinalPrice * tax) / 100
                            orderReport.append(" + $tax % \n")
                        }
                    }

                    orderReport.append("= ${consumerFinalPrice.roundToTwoDecimalPlaces()}\n")
                    orderReport.append("--------------\n")
                }
                return@withContext orderReport.toString()
            }.getOrElse { e ->
                return@withContext null
            }
        }
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
        consumerNameList: List<String>,
    ): String?
}