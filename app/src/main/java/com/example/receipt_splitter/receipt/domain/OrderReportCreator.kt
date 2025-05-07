package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.main.basic.isNotZero
import com.example.receipt_splitter.receipt.presentation.OrderData
import com.example.receipt_splitter.receipt.presentation.ReceiptData
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

                if (receiptData.restaurant.isNotEmpty())
                    orderReport.append("${receiptData.restaurant} \n")
                if (receiptData.date.isNotEmpty())
                    orderReport.append("${receiptData.date} \n")
                orderReport.append("--------------------\n")

                for (splitReceiptData in orderDataList) {
                    if (splitReceiptData.selectedQuantity.isNotZero()) {
                        val sumPrice = splitReceiptData.selectedQuantity * splitReceiptData.price
                        finalPrice += sumPrice
                        orderReport.append(
                            splitReceiptData.name + "     "
                                    + splitReceiptData.selectedQuantity + " x " + splitReceiptData.price + " = "
                                    + sumPrice + "\n"
                        )
                    }
                }

                receiptData.discount?.let { discount ->
                    orderReport.append("-------------\n")
                    finalPrice -= (finalPrice * discount) / 100
                    orderReport.append("- $discount % \n")
                }
                receiptData.tax?.let { tax ->
                    orderReport.append("-------------\n")
                    finalPrice += (finalPrice * tax) / 100
                    orderReport.append("+ $tax % \n")
                }
                orderReport.append("-------------\n")

                orderReport.append(finalPrice)

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