package com.example.receipt_splitter.receipt.data.services

import android.graphics.Bitmap
import com.example.receipt_splitter.main.basic.getLanguageString
import com.example.receipt_splitter.receipt.data.services.DataConstantsReceipt.RESPONSE_MIME_TYPE
import com.example.receipt_splitter.receipt.data.services.DataConstantsReceipt.receiptSchemaObjectNotTranslated
import com.example.receipt_splitter.receipt.data.services.DataConstantsReceipt.receiptSchemaObjectTranslated
import com.example.receipt_splitter.receipt.presentation.ReceiptUiMessage
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI

class ReceiptService() : ReceiptServiceInterface {

    private companion object {
        private const val ONE_IMAGE = 1
        private const val TWO_IMAGES = 2
        private const val THREE_IMAGES = 3
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

    override suspend fun getReceiptJsonFromImages(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        vertexModel: String,
        translateTo: String?,
    ): String {
        return getReceiptJsonFromImagesImpl(
            listOfBitmaps = listOfBitmaps,
            requestText = requestText,
            vertexModel = vertexModel,
            translateTo = translateTo,
        )
    }

    private suspend fun getReceiptJsonFromImagesImpl(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        vertexModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
        translateTo: String?,
    ): String {
        val requestTranslateText = requestText.getLanguageString(translateTo = translateTo)
        val generativeModel = getGenerativeModel(
            vertexJson = if (translateTo == null) receiptSchemaObjectNotTranslated else receiptSchemaObjectTranslated,
            vertexModel = vertexModel,
            mimeType = mimeType,
        )
        val prompt = getPrompt(listOfBitmaps, requestTranslateText)
        val result = generativeModel.generateContent(prompt)
        return result.text ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }

    private fun getGenerativeModel(
        vertexJson: Schema,
        vertexModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
    ) = Firebase.vertexAI.generativeModel(
        modelName = vertexModel,
        generationConfig = generationConfig {
            responseMimeType = mimeType
            responseSchema = vertexJson
        }
    )

    override suspend fun getReceiptJsonFromText(
        receiptText: String,
        requestText: String,
        vertexModel: String,
        translateTo: String?,
    ): String {
        return getReceiptJsonFromTextImpl(
            requestText = requestText,
            vertexModel = vertexModel,
            translateTo = translateTo,
            receiptText = receiptText,
        )
    }

    private suspend fun getReceiptJsonFromTextImpl(
        requestText: String,
        vertexModel: String,
        mimeType: String = RESPONSE_MIME_TYPE,
        translateTo: String?,
        receiptText: String,
    ): String {
        val requestTranslateText = requestText.getLanguageString(translateTo = translateTo)
        val overallRequestText = "$requestTranslateText $receiptText"
        val generativeModel = getGenerativeModel(
            vertexJson = if (translateTo == null) receiptSchemaObjectNotTranslated else receiptSchemaObjectTranslated,
            vertexModel = vertexModel,
            mimeType = mimeType
        )
        val prompt = content { text(overallRequestText) }
        val result = generativeModel.generateContent(prompt)
        return result.text ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }
}

interface ReceiptServiceInterface {
    suspend fun getReceiptJsonFromImages(
        listOfBitmaps: List<Bitmap>,
        requestText: String,
        vertexModel: String,
        translateTo: String?,
    ): String

    suspend fun getReceiptJsonFromText(
        receiptText: String,
        requestText: String,
        vertexModel: String,
        translateTo: String?,
    ): String
}