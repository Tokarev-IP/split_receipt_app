package com.iliatokarev.receipt_splitter.receipt.domain

import com.iliatokarev.receipt_splitter.main.basic.isNotZero
import com.iliatokarev.receipt_splitter.main.basic.roundToTwoDecimalPlaces
import com.iliatokarev.receipt_splitter.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReportCreator() : OrderReportCreatorInterface {

    override suspend fun buildOrderReport(
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
                orderReport.append("--------------\n")

                for (splitReceiptData in orderDataList) {
                    if (splitReceiptData.selectedQuantity.isNotZero()) {
                        val sumPrice = splitReceiptData.selectedQuantity * splitReceiptData.price
                        finalPrice += sumPrice
                        orderReport.append(
                            "${splitReceiptData.name}     ${splitReceiptData.selectedQuantity} x ${splitReceiptData.price}  =  ${sumPrice.roundToTwoDecimalPlaces()}\n"
                        )
                    }
                }
                if (orderDataList.isNotEmpty())
                    orderReport.append(" = ${finalPrice.roundToTwoDecimalPlaces()}\n")

                if (receiptData.discount != null
                    || receiptData.tip != null
                    || receiptData.tax != null
                ) {
                    orderReport.append("------\n")

                    receiptData.discount?.let { discount ->
                        finalPrice -= (finalPrice * discount) / 100
                        orderReport.append("- $discount % \n")
                    }
                    receiptData.tip?.let { tip ->
                        finalPrice += (finalPrice * tip) / 100
                        orderReport.append("+ $tip % \n")
                    }
                    receiptData.tax?.let { tax ->
                        finalPrice += (finalPrice * tax) / 100
                        orderReport.append("+ $tax % \n")
                    }
                    orderReport.append("------\n")
                    orderReport.append(" = ${finalPrice.roundToTwoDecimalPlaces()}\n")
                }

                if (receiptData.additionalSumList.isNotEmpty()) {
                    for (additionalSum in receiptData.additionalSumList) {
                        orderReport.append("${additionalSum.first}     ${additionalSum.second}\n")
                        finalPrice += additionalSum.second
                    }
                    orderReport.append("------\n")
                    orderReport.append(" = ${finalPrice.roundToTwoDecimalPlaces()}\n")
                }

                orderReport.append("--------------\n")
                orderReport.append("${finalPrice.roundToTwoDecimalPlaces()}")

                return@withContext orderReport.toString()
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }
}

interface OrderReportCreatorInterface {
    suspend fun buildOrderReport(
        receiptData: ReceiptData,
        orderDataList: List<OrderData>,
    ): String?
}