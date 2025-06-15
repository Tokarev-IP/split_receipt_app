package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.label.ImageLabel
import com.iliatokarev.receipt_splitter_app.main.basic.convertMillisToMinutes
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.APPROPRIATE_LABELS
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_AMOUNT_OF_DISHES
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.MAXIMUM_TEXT_LENGTH
import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt.ONE_ATTEMPT
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageConverterInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ImageLabelingKitInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.services.ReceiptServiceInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FirebaseUserIdInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptDataJson
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptVertexData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.UserAttemptsData
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
        folderId: Long?,
    ): ReceiptCreationResult {
        return convertReceiptFromImageImpl(
            listOfImages = listOfImages,
            translateTo = translateTo,
            folderId = folderId,
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
        folderId: Long?,
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
            val remainingAttempts = receiptVertexConstants.maximumAttemptsForUser - userAttempts
            val receiptJson = receiptService.getReceiptJsonFromImages(
                listOfBitmaps = listOfBitmaps,
                requestText = receiptVertexConstants.requestText,
                aiModel = receiptVertexConstants.aiModel,
                translateTo = translateTo,
            )
            val receiptDataJson: ReceiptDataJson =
                Json.decodeFromString<ReceiptDataJson>(receiptJson)
            val correctedReceiptDataJson = correctReceiptDataJson(receiptDataJson)
            if (correctedReceiptDataJson.orders.size > MAXIMUM_AMOUNT_OF_DISHES)
                return@withContext ReceiptCreationResult.ReceiptIsTooBig
            if (correctedReceiptDataJson.orders.isEmpty())
                return@withContext ReceiptCreationResult.ImageIsInappropriate
            val receiptId: Long = receiptDbRepository
                .insertReceiptDataJson(
                    receiptDataJson = correctedReceiptDataJson,
                    folderId = folderId,
                )
            return@withContext ReceiptCreationResult.Success(
                receiptId = receiptId,
                remainingAttempts = remainingAttempts,
            )
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

    private fun correctReceiptDataJson(
        receiptDataJson: ReceiptDataJson,
    ): ReceiptDataJson {
        return receiptDataJson.copy(
            receiptName = receiptDataJson.receiptName.take(MAXIMUM_TEXT_LENGTH),
            translatedReceiptName = receiptDataJson.translatedReceiptName?.take(MAXIMUM_TEXT_LENGTH),
            date = receiptDataJson.date.take(MAXIMUM_TEXT_LENGTH),
            orders = receiptDataJson.orders.take(MAXIMUM_AMOUNT_OF_DISHES).map { order ->
                order.copy(
                    name = order.name.take(MAXIMUM_TEXT_LENGTH),
                    translatedName = order.translatedName?.take(MAXIMUM_TEXT_LENGTH),
                    quantity = order.quantity.takeIf { order.quantity <= DataConstantsReceipt.MAXIMUM_AMOUNT_OF_DISH_QUANTITY }
                        ?: DataConstantsReceipt.MAXIMUM_AMOUNT_OF_DISH_QUANTITY,
                    price = order.price.takeIf { order.price <= DataConstantsReceipt.MAXIMUM_SUM }
                        ?: DataConstantsReceipt.MAXIMUM_SUM.toFloat()
                )
            },
            total = receiptDataJson.total.takeIf { receiptDataJson.total <= DataConstantsReceipt.MAXIMUM_SUM }
                ?: DataConstantsReceipt.MAXIMUM_SUM.toFloat(),
            tax = if (receiptDataJson.tax == null) receiptDataJson.tax else
                receiptDataJson.tax.takeIf { receiptDataJson.tax <= DataConstantsReceipt.MAXIMUM_PERCENT }
                    ?: DataConstantsReceipt.MAXIMUM_PERCENT.toFloat(),
            discount = if (receiptDataJson.discount == null) receiptDataJson.discount else
                receiptDataJson.discount.takeIf { receiptDataJson.discount <= DataConstantsReceipt.MAXIMUM_PERCENT }
                    ?: DataConstantsReceipt.MAXIMUM_PERCENT.toFloat(),
            tip = if (receiptDataJson.tip == null) receiptDataJson.tip else
                receiptDataJson.tip.takeIf { receiptDataJson.tip <= DataConstantsReceipt.MAXIMUM_PERCENT }
                    ?: DataConstantsReceipt.MAXIMUM_PERCENT.toFloat(),
        )
    }
}

interface CreateReceiptUseCaseInterface {
    suspend fun createReceiptFromUriImage(
        listOfImages: List<Uri>,
        translateTo: String?,
        folderId: Long?,
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
    object ReceiptIsTooBig : ReceiptCreationResult
}