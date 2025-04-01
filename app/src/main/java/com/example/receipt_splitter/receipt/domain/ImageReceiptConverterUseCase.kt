package com.example.receipt_splitter.receipt.domain

import android.net.Uri
import com.example.receipt_splitter.receipt.data.ImageConverterInterface
import com.example.receipt_splitter.receipt.data.ImageLabelingKitInterface
import com.example.receipt_splitter.receipt.data.ReceiptRepositoryInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.room.ReceiptDbRepositoryInterface
import com.google.mlkit.vision.label.ImageLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ImageReceiptConverterUseCase(
    private val imageLabelingKit: ImageLabelingKitInterface,
    private val imageConverter: ImageConverterInterface,
    private val receiptRepository: ReceiptRepositoryInterface,
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
) : ImageReceiptConverterUseCaseInterface {

    private companion object {
        private const val REQUEST_TEXT =
            "Read this receipt without spaces and extract using English"

        // Labels for images are the following:
        // https://developers.google.com/ml-kit/vision/image-labeling/label-map
        // 135 Menu 240 Receipt 273 Paper 93 Poster
        private val APPROPRIATE_LABELS: List<Int> = listOf(273, 135, 240, 93)
    }

    override suspend fun convertReceiptFromImage(image: Uri): ImageReceiptConverterUseCaseResponse {
        return convertReceiptFromImageImpl(image = image)
    }

    private suspend fun convertReceiptFromImageImpl(
        image: Uri,
        requestText: String = REQUEST_TEXT,
    ): ImageReceiptConverterUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val imageBitmap = imageConverter.convertImageFromUriToBitmap(image)
                val imageLabels = imageLabelingKit.getLabelsOfImage(imageBitmap)
                val isReceiptAppropriate = hasAppropriateLabel(imageLabels)
                if (isReceiptAppropriate) {
                    val receiptJsonString: String? =
                        receiptRepository.getReceiptJsonString(imageBitmap, requestText)
                    if (receiptJsonString != null) {
                        val receiptDataJson = Json.decodeFromString<ReceiptDataJson>(receiptJsonString)
                        if (receiptDataJson.orders.isNotEmpty()) {
                            receiptDbRepository.insertReceiptData(receiptDataJson)
                            return@withContext ImageReceiptConverterUseCaseResponse.Success
                        } else
                            return@withContext ImageReceiptConverterUseCaseResponse.ImageIsInappropriate
                    } else
                        return@withContext ImageReceiptConverterUseCaseResponse.JsonError
                } else
                    return@withContext ImageReceiptConverterUseCaseResponse.ImageIsInappropriate
            }.getOrElse { e: Throwable ->
                return@withContext ImageReceiptConverterUseCaseResponse.Error(
                    e.message ?: "Some errors"
                )
            }
        }
    }

    private fun hasAppropriateLabel(
        listOfLabels: List<ImageLabel>,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean {
        for (label in listOfLabels) {
            if (label.index in appropriateLabels) {
                return true
                break
            }
        }
        return false
    }
}

interface ImageReceiptConverterUseCaseInterface {
    suspend fun convertReceiptFromImage(image: Uri): ImageReceiptConverterUseCaseResponse
}

sealed interface ImageReceiptConverterUseCaseResponse {
    object Success : ImageReceiptConverterUseCaseResponse
    object ImageIsInappropriate : ImageReceiptConverterUseCaseResponse
    object JsonError : ImageReceiptConverterUseCaseResponse
    class Error(val msg: String) : ImageReceiptConverterUseCaseResponse
}
