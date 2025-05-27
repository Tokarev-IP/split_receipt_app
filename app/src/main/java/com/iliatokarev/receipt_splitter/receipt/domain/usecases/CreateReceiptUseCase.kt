package com.iliatokarev.receipt_splitter.receipt.domain.usecases

import android.graphics.Bitmap
import android.net.Uri
import com.iliatokarev.receipt_splitter.main.basic.convertMillisToMinutes
import com.iliatokarev.receipt_splitter.receipt.data.room.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter.receipt.data.services.DataConstantsReceipt.APPROPRIATE_LABELS
import com.iliatokarev.receipt_splitter.receipt.data.services.DataConstantsReceipt.ONE_ATTEMPT
import com.iliatokarev.receipt_splitter.receipt.data.services.ImageConverterInterface
import com.iliatokarev.receipt_splitter.receipt.data.services.ImageLabelingKitInterface
import com.iliatokarev.receipt_splitter.receipt.data.services.ReceiptServiceInterface
import com.iliatokarev.receipt_splitter.receipt.data.store.FireStoreRepositoryInterface
import com.iliatokarev.receipt_splitter.receipt.data.store.FirebaseUserIdInterface
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptDataJson
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptUiMessage
import com.iliatokarev.receipt_splitter.receipt.presentation.ReceiptVertexData
import com.iliatokarev.receipt_splitter.receipt.presentation.UserAttemptsData
import com.google.mlkit.vision.label.ImageLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CreateReceiptUseCase(
    private val imageLabelingKit: ImageLabelingKitInterface,
    private val imageConverter: ImageConverterInterface,
    private val receiptService: ReceiptServiceInterface,
    private val receiptDbRepository: ReceiptDbRepositoryInterface,
    private val firestoreRepository: FireStoreRepositoryInterface,
    private val firebaseUserId: FirebaseUserIdInterface,
) : CreateReceiptUseCaseInterface {

    private companion object {
        private const val DELAY_TIME = 3000L
    }

    override suspend fun createReceiptFromUriImage(
        listOfImages: List<Uri>,
        translateTo: String?,
    ): ReceiptCreationResult {
        return convertReceiptFromImageImpl(
            listOfImages = listOfImages,
            translateTo = translateTo,
        )
    }

    override suspend fun haveImagesGotNotAppropriateImages(images: List<Uri>): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                val bitmapImages = images.map {
                    imageConverter.convertImageFromUriToBitmap(it)
                }
                hasNotAppropriateLabel(bitmapImages)
            }.getOrElse {
                true
            }
        }

    private suspend fun convertReceiptFromImageImpl(
        listOfImages: List<Uri>,
        translateTo: String?,
    ): ReceiptCreationResult = withContext(Dispatchers.IO) {
        runCatching {
            val listOfBitmaps = listOfImages.map { image ->
                imageConverter.convertImageFromUriToBitmap(image)
            }
            if (hasNotAppropriateLabel(listOfBitmaps)) {
                delay(DELAY_TIME)
                return@withContext ReceiptCreationResult.ImageIsInappropriate
            }
            val userId = getUserId() ?: return@withContext ReceiptCreationResult.LoginRequired
            val receiptVertexConstants: ReceiptVertexData =
                firestoreRepository.getReceiptVertexConstants()
            val userAttempts = manageAttemptsToCreateReceipt(
                userId = userId,
                maximumAttemptsForUser = receiptVertexConstants.maximumAttemptsForUser,
                deltaTimeBetweenAttempts = receiptVertexConstants.deltaTimeBetweenAttempts,
            )
            if (userAttempts > receiptVertexConstants.maximumAttemptsForUser) {
                delay(DELAY_TIME)
                return@withContext ReceiptCreationResult.TooManyAttempts(
                    remainingTime = receiptVertexConstants.deltaTimeBetweenAttempts.convertMillisToMinutes()
                )
            }
            val receiptJson = receiptService.getReceiptJsonFromImages(
                listOfBitmaps = listOfBitmaps,
                requestText = receiptVertexConstants.requestText,
                vertexModel = receiptVertexConstants.vertexModel,
                translateTo = translateTo,
            )
            val receiptDataJson = Json.decodeFromString<ReceiptDataJson>(receiptJson)
            if (receiptDataJson.orders.isNotEmpty()) {
                val receiptId: Long = receiptDbRepository.insertReceiptDataJson(receiptDataJson)
                return@withContext ReceiptCreationResult.Success(
                    receiptId = receiptId,
                    remainingAttempts = receiptVertexConstants.maximumAttemptsForUser - userAttempts,
                )
            } else {
                return@withContext ReceiptCreationResult.ImageIsInappropriate
            }
        }.getOrElse { e: Throwable ->
            delay(DELAY_TIME)
            return@withContext ReceiptCreationResult.Error(
                e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg
            )
        }
    }

    private suspend fun manageAttemptsToCreateReceipt(
        userId: String,
        oneAttempt: Int = ONE_ATTEMPT,
        deltaTimeBetweenAttempts: Long,
        maximumAttemptsForUser: Int,
    ): Int {
        val userAttemptsData: UserAttemptsData? =
            firestoreRepository.getUserAttemptsData(documentId = userId)
        userAttemptsData?.let { userAttempts ->
            val currentTime = System.currentTimeMillis()
            val userLastAttemptTime = userAttempts.lastAttemptTime
            if (currentTime - userLastAttemptTime < deltaTimeBetweenAttempts) {
                if (userAttempts.attempts >= maximumAttemptsForUser)
                    return userAttempts.attempts + oneAttempt
                else
                    firestoreRepository.putUserAttemptsData(
                        documentId = userId,
                        data = UserAttemptsData(
                            lastAttemptTime = currentTime,
                            attempts = userAttempts.attempts + oneAttempt,
                        )
                    )
                return userAttempts.attempts + oneAttempt
            } else {
                firestoreRepository.putUserAttemptsData(
                    documentId = userId,
                    data = UserAttemptsData(
                        lastAttemptTime = currentTime,
                        attempts = oneAttempt,
                    )
                )
                return oneAttempt
            }
        } ?: run {
            firestoreRepository.putUserAttemptsData(
                documentId = userId,
                data = UserAttemptsData(
                    lastAttemptTime = System.currentTimeMillis(),
                    attempts = oneAttempt,
                )
            )
            return oneAttempt
        }
    }

    private suspend fun hasNotAppropriateLabel(
        imagesBitmap: List<Bitmap>,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean =
        withContext(Dispatchers.Default) {
            runCatching {
                for (imageBitmap in imagesBitmap) {
                    val listOfLabels = imageLabelingKit.getLabelsOfImage(imageBitmap)
                    val isAppropriate = isInLabels(listOfLabels, appropriateLabels)
                    if (isAppropriate == false)
                        true
                }
                false
            }.getOrElse {
                true
            }
        }

    private fun isInLabels(
        listOfLabels: List<ImageLabel>,
        appropriateLabels: List<Int> = APPROPRIATE_LABELS,
    ): Boolean {
        for (label in listOfLabels) {
            if (label.index in appropriateLabels)
                return true
        }
        return false
    }

    override fun filterBySize(listOfImages: List<Uri>): List<Uri> {
        return listOfImages.take(DataConstantsReceipt.MAXIMUM_AMOUNT_OF_IMAGES)
    }

    private fun getUserId(): String? {
        return firebaseUserId.getUserId()
    }
}

interface CreateReceiptUseCaseInterface {
    suspend fun createReceiptFromUriImage(
        listOfImages: List<Uri>,
        translateTo: String?,
    ): ReceiptCreationResult

    suspend fun haveImagesGotNotAppropriateImages(listOfImages: List<Uri>): Boolean
    fun filterBySize(listOfImages: List<Uri>): List<Uri>
}

sealed interface ReceiptCreationResult {
    class Success(val receiptId: Long, val remainingAttempts: Int) : ReceiptCreationResult
    object ImageIsInappropriate : ReceiptCreationResult
    class Error(val msg: String) : ReceiptCreationResult
    class TooManyAttempts(val remainingTime: Int) : ReceiptCreationResult
    object LoginRequired : ReceiptCreationResult
}