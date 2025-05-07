package com.example.receipt_splitter.receipt.domain.usecases

import android.graphics.Bitmap
import android.net.Uri
import com.example.receipt_splitter.receipt.data.DataConstantsReceipt
import com.example.receipt_splitter.receipt.data.ImageConverterInterface
import com.example.receipt_splitter.receipt.data.ImageLabelingKitInterface
import com.example.receipt_splitter.receipt.data.ReceiptConverterInterface
import com.example.receipt_splitter.receipt.data.ReceiptServiceInterface
import com.example.receipt_splitter.receipt.data.room.ReceiptDbRepositoryInterface
import com.example.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.example.receipt_splitter.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CreateReceiptUseCase(
    private val imageLabelingKit: ImageLabelingKitInterface,
    private val imageConverter: ImageConverterInterface,
    private val receiptRepository: ReceiptServiceInterface,
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
    private val receiptConverter: ReceiptConverterInterface,
) : CreateReceiptUseCaseInterface {

    override suspend fun createReceiptFromUriImage(listOfImages: List<Uri>): ReceiptCreationResult {
        return convertReceiptFromImageImpl(listOfImages = listOfImages)
    }

    override suspend fun areAllUriImagesAppropriate(images: List<Uri>): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                for (imageUri in images) {
                    val imageBitmap = imageConverter.convertImageFromUriToBitmap(imageUri)
                    hasAppropriateLabel(imageBitmap)
                }
                return@withContext true
            }.getOrElse {
                return@withContext false
            }
        }

    private suspend fun convertReceiptFromImageImpl(
        listOfImages: List<Uri>,
        requestText: String = REQUEST_TEXT_WITH_TEXT,
    ): ReceiptCreationResult = withContext(Dispatchers.IO) {
        runCatching {
            val listOfBitmaps = listOfImages.map { image ->
                imageConverter.convertImageFromUriToBitmap(image)
            }
            if (containsNoAppropriateImage(listOfBitmaps)) {
                return@withContext ReceiptCreationResult.ImageIsInappropriate
            }
            val receiptText = receiptConverter.convertReceiptImagesToText(listOfImages)
            val receiptJsonString = receiptRepository.getReceiptJsonStringFromText(
                receiptText = receiptText,
                requestText = requestText
            )
            val receiptDataJson = Json.decodeFromString<ReceiptDataJson>(receiptJsonString)
            if (receiptDataJson.orders.isNotEmpty()) {
                val receiptId: Long = receiptDbRepository.insertReceiptDataJson(receiptDataJson)
                return@withContext ReceiptCreationResult.Success(receiptId)
            } else
                return@withContext ReceiptCreationResult.ImageIsInappropriate
        }.getOrElse { e: Throwable ->
            return@withContext ReceiptCreationResult.Error(
                e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
            )
        }
    }

    private suspend fun containsNoAppropriateImage(imagesBitmap: List<Bitmap>): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                for (imageBitmap in imagesBitmap) {
                    if (hasAppropriateLabel(imageBitmap) == false)
                        return@withContext true
                }
                return@withContext false
            }.getOrElse {
                return@withContext true
            }
        }

    private suspend fun hasAppropriateLabel(
        bitmapImage: Bitmap,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean {
        val listOfLabels = imageLabelingKit.getLabelsOfImage(bitmapImage)
        for (label in listOfLabels) {
            if (label.index in appropriateLabels) {
                return true
                break
            }
        }
        return false
    }

    override fun filterBySize(listOfImages: List<Uri>): List<Uri> {
        return listOfImages.take(MAXIMUM_NUMBER_OF_IMAGES)
    }

    private companion object {
        private const val MAXIMUM_NUMBER_OF_IMAGES = DataConstantsReceipt.MAX_AMOUNT_OF_IMAGES
        private const val REQUEST_TEXT_WITH_IMAGES =
            "Read this images of the one receipt without spaces and extract using English"
        private const val REQUEST_TEXT_WITH_TEXT =
            "There is a text of the receipt.Extract all information from it.Take the final price after all possible sales for every order."

        // Labels for images are the following:
        // https://developers.google.com/ml-kit/vision/image-labeling/label-map
        // 135 Menu 240 Receipt 273 Paper 93 Poster
        private val APPROPRIATE_LABELS: List<Int> = listOf(273, 135, 240, 93)
    }
}

interface CreateReceiptUseCaseInterface {
    suspend fun createReceiptFromUriImage(listOfImages: List<Uri>): ReceiptCreationResult
    suspend fun areAllUriImagesAppropriate(listOfImages: List<Uri>): Boolean
    fun filterBySize(listOfImages: List<Uri>): List<Uri>
}

sealed interface ReceiptCreationResult {
    class Success(val receiptId: Long) : ReceiptCreationResult
    object ImageIsInappropriate : ReceiptCreationResult
    class Error(val msg: String) : ReceiptCreationResult
}