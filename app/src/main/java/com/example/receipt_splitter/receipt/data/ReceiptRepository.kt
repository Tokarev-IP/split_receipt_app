package com.example.receipt_splitter.receipt.data

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Content
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

        private const val ONE_IMAGE = 1
        private const val TWO_IMAGES = 2
        private const val THREE_IMAGES = 3
    }

    private suspend fun getReceiptJsonStringImpl(
        listOfBitmaps: List<Bitmap>,
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

        val prompt = getPrompt(listOfBitmaps, requestText)
        val result = generativeModel.generateContent(prompt)
        return result.text
    }

    private fun getPrompt(listOfBitmaps: List<Bitmap>, requestText: String): Content {
        return when (listOfBitmaps.size) {
            ONE_IMAGE -> {
                content {
                    image(listOfBitmaps[0])
                    text(requestText)
                }
            }

            TWO_IMAGES -> {
                content {
                    image(listOfBitmaps[0])
                    image(listOfBitmaps[1])
                    text(requestText)
                }
            }

            THREE_IMAGES -> {
                content {
                    image(listOfBitmaps[0])
                    image(listOfBitmaps[1])
                    image(listOfBitmaps[2])
                    text(requestText)
                }
            }

            else -> {
                content {
                    text(requestText)
                }
            }
        }
    }

    override suspend fun getReceiptJsonString(
        listOfBitmaps: List<Bitmap>,
        requestText: String
    ): String? {
        return getReceiptJsonStringImpl(
            listOfBitmaps = listOfBitmaps,
            requestText = requestText
        )
    }
}

interface ReceiptRepositoryInterface {
    suspend fun getReceiptJsonString(listOfBitmaps: List<Bitmap>, requestText: String): String?
}