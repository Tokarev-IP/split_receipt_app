package com.iliatokarev.receipt_splitter.receipt.presentation

import androidx.compose.runtime.Immutable
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReceiptDataJson(
    @SerialName("receipt_name")
    val receiptName: String = "no name",
    @SerialName("translated_receipt_name")
    val translatedReceiptName: String? = null,
    @SerialName("date")
    val date: String = "no date",
    @SerialName("orders")
    val orders: List<OrderDataJson> = emptyList<OrderDataJson>(),
    @SerialName("total_sum")
    val total: Float = 0.0F,
    @SerialName("tax_in_percent")
    val tax: Float? = null,
    @SerialName("discount_in_percent")
    val discount: Float? = null,
    @SerialName("tip_in_percent")
    val tip: Float? = null,
)

@Serializable
class OrderDataJson(
    @SerialName("name")
    val name: String,
    @SerialName("translated_name")
    val translatedName: String? = null,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Float,
)

@Immutable
data class ReceiptData(
    val id: Long,
    val receiptName: String = "no name",
    val translatedReceiptName: String? = null,
    val date: String = "no date",
    val total: Float = 0.0F,
    val tax: Float? = null,
    val discount: Float? = null,
    val tip: Float? = null,
    val additionalSumList: List<Pair<String, Float>> = emptyList(),
)

@Immutable
data class OrderData(
    val id: Long,
    val name: String = "no name",
    val translatedName: String? = null,
    val selectedQuantity: Int = 0,
    val quantity: Int = 1,
    val price: Float = 0f,
    val receiptId: Long,
)

@Serializable
class UserAttemptsData(
    @PropertyName("lastAttemptTime")
    val lastAttemptTime: Long = 0L,
    @PropertyName("attempts")
    val attempts: Int = 1,
)

@Serializable
class ReceiptVertexData(
    @PropertyName("deltaTimeBetweenAttempts")
    val deltaTimeBetweenAttempts: Long = 0L,
    @PropertyName("maximumAttemptsForUser")
    val maximumAttemptsForUser: Int = 0,
    @PropertyName("requestText")
    val requestText: String = "",
    @PropertyName("vertexModel")
    val vertexModel: String = "",
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

object ReceiptUIConstants {
    const val ONE_LINE = 1
    const val ONE_ELEMENT = 1
}