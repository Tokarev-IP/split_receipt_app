package com.example.receipt_splitter.receipt.data

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI

class ReceiptRepository() : ReceiptRepositoryInterface {

    private companion object {
        private val vertexReceiptJson = Schema.obj(
            mapOf(
                "restaurant_name" to Schema.string(),
                "date_dd_mm-yyyy" to Schema.string(),
                "sub_total_sum" to Schema.float(),
                "total_sum" to Schema.float(),
                "tax_in_percent" to Schema.float(),
                "discount_in_percent" to Schema.float(),
                "orders" to Schema.array(
                    Schema.obj(
                        mapOf(
                            "name" to Schema.string(),
                            "quantity" to Schema.integer(),
                            "price" to Schema.float(),
                        ),
                    )
                )
            ),
            optionalProperties = listOf(
                "restaurant_name",
                "date_dd_mm-yyyy",
                "sub_total_sum",
                "tax_in_percent",
                "discount_in_percent"
            )
        )

        private const val RESPONSE_MIME_TYPE = "application/json"
    }

    private suspend fun getReceiptJsonStringImpl(
        bitmap: Bitmap,
        requestText: String,
        vertexJson: Schema = vertexReceiptJson,
        vertexAiModel: String = DataConstantsReceipt.VERTEX_AI_MODEL,
        mimeType: String = RESPONSE_MIME_TYPE,
    ): String? {
        val generativeModel =
            Firebase.vertexAI.generativeModel(
                modelName = vertexAiModel,
                generationConfig = generationConfig {
                    responseMimeType = mimeType
                    responseSchema = vertexJson
                }
            )

        val prompt = content {
            image(bitmap)
            text(requestText)
        }
        val result = generativeModel.generateContent(prompt)
        return result.text
    }

    override suspend fun getReceiptJsonString(
        bitmap: Bitmap,
        requestText: String
    ): String? {
        return getReceiptJsonStringImpl(
            bitmap = bitmap,
            requestText = requestText
        )
    }
}

interface ReceiptRepositoryInterface {
    suspend fun getReceiptJsonString(bitmap: Bitmap, requestText: String): String?
}