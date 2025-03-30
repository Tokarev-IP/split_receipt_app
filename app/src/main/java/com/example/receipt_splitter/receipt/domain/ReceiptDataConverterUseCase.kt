package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.receipt.presentation.ReceiptData
import com.example.receipt_splitter.receipt.presentation.SplitOrderData

class ReceiptDataConverterUseCase() : ReceiptDataConverterUseCaseInterface {

    override fun convertReceiptDataToSplitData(receiptData: ReceiptData): List<SplitOrderData> {
        return receiptData.orders.map { order ->
            SplitOrderData(
                name = order.name,
                quantity = order.quantity,
                price = order.price,
            )
        }
    }

    override fun addQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderName: String,
    ): List<SplitOrderData> {
        return splitOrderDataList.map { currentData ->
                if (currentData.name == orderName && currentData.selectedQuantity < currentData.quantity) {
                    currentData.let {
                        val updatedQuantity = it.selectedQuantity + 1
                        it.copy(selectedQuantity = updatedQuantity)
                    }
                } else {
                    currentData
                }
            }
        }

    override fun subtractQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderName: String
    ): List<SplitOrderData> {
        return splitOrderDataList.map { currentData ->
            if (currentData.name == orderName && currentData.selectedQuantity > 0) {
                currentData.let {
                    val updatedQuantity = it.selectedQuantity - 1
                    it.copy(selectedQuantity = updatedQuantity)
                }
            } else {
                currentData
            }
        }
    }
}

interface ReceiptDataConverterUseCaseInterface {
    fun convertReceiptDataToSplitData(receiptData: ReceiptData): List<SplitOrderData>
    fun addQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderName: String,
    ): List<SplitOrderData>

    fun subtractQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderName: String,
    ): List<SplitOrderData>
}