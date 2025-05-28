package com.iliatokarev.receipt_splitter_app.receipt.domain

import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData

class OrderDataSplitter() : OrderDataSplitterInterface {

    override fun addQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData> {
        runCatching {
            return orderDataList.map { currentData ->
                if (currentData.id == orderId && currentData.selectedQuantity < currentData.quantity) {
                    currentData.let {
                        val updatedQuantity = it.selectedQuantity + 1
                        it.copy(selectedQuantity = updatedQuantity)
                    }
                } else {
                    currentData
                }
            }
        }.getOrElse { e: Throwable ->
            return orderDataList
        }
    }

    override fun subtractQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData> {
        runCatching {
            return orderDataList.map { currentData ->
                if (currentData.id == orderId && currentData.selectedQuantity > 0) {
                    currentData.let {
                        val updatedQuantity = it.selectedQuantity - 1
                        it.copy(selectedQuantity = updatedQuantity)
                    }
                } else {
                    currentData
                }
            }
        }.getOrElse { e: Throwable ->
            return orderDataList
        }
    }
}

interface OrderDataSplitterInterface {
    fun addQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData>

    fun subtractQuantityToSpecificOrderData(
        orderDataList: List<OrderData>,
        orderId: Long,
    ): List<OrderData>
}