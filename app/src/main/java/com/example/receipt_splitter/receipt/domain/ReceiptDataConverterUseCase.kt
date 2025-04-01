package com.example.receipt_splitter.receipt.domain

import com.example.receipt_splitter.receipt.presentation.SplitOrderData
import com.example.receipt_splitter.receipt.presentation.SplitReceiptData

class ReceiptDataConverterUseCase() : ReceiptDataConverterUseCaseInterface {

    override fun convertReceiptDataToSplitData(splitReceiptData: SplitReceiptData): List<SplitOrderData> {
        return splitReceiptData.orders.map { order ->
            SplitOrderData(
                id = order.id,
                name = order.name,
                quantity = order.quantity,
                price = order.price,
            )
        }
    }

    override fun addQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderId: Long,
    ): List<SplitOrderData> {
        return splitOrderDataList.map { currentData ->
                if (currentData.id == orderId && currentData.selectedQuantity < currentData.quantity) {
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
        orderId: Long,
    ): List<SplitOrderData> {
        return splitOrderDataList.map { currentData ->
            if (currentData.id == orderId && currentData.selectedQuantity > 0) {
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
    fun convertReceiptDataToSplitData(splitReceiptData: SplitReceiptData): List<SplitOrderData>
    fun addQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderId: Long,
    ): List<SplitOrderData>

    fun subtractQuantityToSplitOrderData(
        splitOrderDataList: List<SplitOrderData>,
        orderId: Long,
    ): List<SplitOrderData>
}