package com.example.receipt_splitter.receipt.presentation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReceiptDataJson(
    @SerialName("restaurant_name")
    val restaurant: String = "no name",
    @SerialName("date_dd/mm/yyyy")
    val date: String = "no date",
    @SerialName("orders")
    val orders: List<OrderDataJson> = emptyList<OrderDataJson>(),
    @SerialName("final_total_sum")
    val total: Float = 0.0F,
    @SerialName("final_tax_in_percent")
    val tax: Float? = null,
    @SerialName("final_discount_in_percent")
    val discount: Float? = null,
    @SerialName("final_tip_in_percent")
    val tip: Float? = null,
    @SerialName("total_tip_sum")
    val tipSum: Float? = null,
)

@Serializable
class OrderDataJson(
    @SerialName("name")
    val name: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Float,
)

data class ReceiptData(
    val id: Long,
    val restaurant: String = "no name",
    val translatedRestaurant: String? = null,
    val date: String = "no date",
    val total: Float = 0.0F,
    val tax: Float? = null,
    val discount: Float? = null,
    val tip: Float? = null,
    val tipSum: Float? = null,
)

data class OrderData(
    val id: Long,
    val name: String = "no name",
    val translatedName: String? = null,
    val selectedQuantity: Int = 0,
    val quantity: Int = 1,
    val price: Float = 0f,
    val receiptId: Long,
)

sealed interface ReceiptNavHostDestinations {
    @Serializable
    object CreateReceiptScreenNav : ReceiptNavHostDestinations

    @Serializable
    object AllReceiptsScreenNav : ReceiptNavHostDestinations

    @Serializable
    class SplitReceiptScreenNav(val receiptId: Long) : ReceiptNavHostDestinations

    @Serializable
    class EditReceiptScreenNav(val receiptId: Long) : ReceiptNavHostDestinations
}