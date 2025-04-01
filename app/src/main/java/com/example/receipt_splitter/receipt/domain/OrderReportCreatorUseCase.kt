package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.main.basic.isNotZero
import com.example.receipt_splitter.receipt.presentation.SplitOrderData
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderReportCreatorUseCase() : OrderReportCreatorUseCaseInterface {

    override suspend fun buildOrderReport(
        receiptData: SplitReceiptData,
        splitOrderDataList: List<SplitOrderData>,
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

                for (splitReceiptData in splitOrderDataList) {
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
                if (orderReport.isBlank())
                    return@withContext null
                else {
                    if (receiptData.discount != null || receiptData.tax != null)
                        orderReport.append("--------------------\n")

                    receiptData.discount?.let { discount ->
                        finalPrice -= (finalPrice * discount) / 100
                        orderReport.append("- $discount % \n")
                    }
                    receiptData.tax?.let { tax ->
                        finalPrice += (finalPrice * tax) / 100
                        orderReport.append("+ $tax % \n")
                    }
                    orderReport.append("--------------------\n")

                    orderReport.append(finalPrice)

                    return@withContext orderReport.toString()
                }
            }.getOrElse { e ->
                return@withContext null
            }
        }
    }
}

interface OrderReportCreatorUseCaseInterface {
    suspend fun buildOrderReport(
        receiptData: SplitReceiptData,
        splitOrderDataList: List<SplitOrderData>,
    ): String?
}