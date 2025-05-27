package com.iliatokarev.receipt_splitter.receipt.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter.main.basic.BasicViewModel
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.iliatokarev.receipt_splitter.receipt.domain.usecases.ReceiptCreationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateReceiptViewModel(
    private val createReceiptUseCase: CreateReceiptUseCaseInterface,
) : BasicViewModel<
        CreateReceiptUiState,
        CreateReceiptIntent,
        CreateReceiptEvent,
        CreateReceiptUiMessageIntent>(initialUiState = CreateReceiptUiState.Show) {

    private val receiptImagesFlow = MutableStateFlow<List<Uri>?>(null)
    private val receiptImagesState = receiptImagesFlow.asStateFlow()

    private fun setReceiptImages(newReceiptImages: List<Uri>?) {
        receiptImagesFlow.value = newReceiptImages
    }

    fun getReceiptImages() = receiptImagesState

    override fun setEvent(newEvent: CreateReceiptEvent) {
        when (newEvent) {
            is CreateReceiptEvent.CreateReceipt -> {
                receiptImagesState.value?.let {
                    createReceipt(
                        listOfImages = it,
                        translateTo = newEvent.translateTo,
                    )
                } ?: setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            }

            is CreateReceiptEvent.PutImages -> {
                putReceiptImages(listOfImages = newEvent.listOfImages)
            }
        }
    }

    private fun createReceipt(
        listOfImages: List<Uri>,
        translateTo: String?,
    ) {
        viewModelScope.launch {
            setUiState(CreateReceiptUiState.Loading)
            val response =
                createReceiptUseCase.createReceiptFromUriImage(
                    listOfImages = listOfImages,
                    translateTo = translateTo,
                )
            when (response) {
                is ReceiptCreationResult.Success -> {
                    setUiMessageIntent(CreateReceiptUiMessageIntent.AttemptsLeft(response.remainingAttempts))
                    setIntent(CreateReceiptIntent.NewReceiptIsCreated(response.receiptId))
                }

                is ReceiptCreationResult.ImageIsInappropriate -> {
                    setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
                    setUiState(CreateReceiptUiState.Show)
                }

                is ReceiptCreationResult.Error -> {
                    setUiMessageIntent(handleReceiptCreationError(response.msg))
                    setUiState(CreateReceiptUiState.Show)
                }

                is ReceiptCreationResult.TooManyAttempts -> {
                    setUiMessageIntent(CreateReceiptUiMessageIntent.TooManyAttempts(response.remainingTime))
                    setUiState(CreateReceiptUiState.Show)
                }

                is ReceiptCreationResult.LoginRequired -> {
                    setUiMessageIntent(CreateReceiptUiMessageIntent.LoginRequired)
                    setUiState(CreateReceiptUiState.Show)
                    setIntent(CreateReceiptIntent.UserIsEmpty)
                }
            }
        }
    }

    private fun putReceiptImages(listOfImages: List<Uri>) {
        viewModelScope.launch {
            val filteredListOfImages =
                createReceiptUseCase.filterBySize(listOfImages = listOfImages)
            val result: Boolean =
                createReceiptUseCase.haveImagesGotNotAppropriateImages(listOfImages = listOfImages)
            if (result == true) {
                setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            }
            setReceiptImages(filteredListOfImages)
        }
    }
}

private fun handleReceiptCreationError(errorMsg: String): CreateReceiptUiMessageIntent {
    return when (errorMsg) {
        CreateReceiptUiMessage.INTERNAL_ERROR.message -> CreateReceiptUiMessageIntent.InternalError
        CreateReceiptUiMessage.NETWORK_ERROR.message -> CreateReceiptUiMessageIntent.InternetConnectionError
        CreateReceiptUiMessage.IMAGE_IS_INAPPROPRIATE.message -> CreateReceiptUiMessageIntent.SomeImagesAreInappropriate
        else -> CreateReceiptUiMessageIntent.InternalError
    }
}

interface CreateReceiptUiState : BasicUiState {
    object Show : CreateReceiptUiState
    object Loading : CreateReceiptUiState
}

sealed interface CreateReceiptEvent : BasicEvent {
    class CreateReceipt(val translateTo: String?) : CreateReceiptEvent
    class PutImages(val listOfImages: List<Uri>) : CreateReceiptEvent
}

interface CreateReceiptUiMessageIntent : BasicUiMessageIntent {
    object SomeImagesAreInappropriate : CreateReceiptUiMessageIntent
    object InternalError : CreateReceiptUiMessageIntent
    object InternetConnectionError : CreateReceiptUiMessageIntent
    class TooManyAttempts(val resetTimeMin: Int) : CreateReceiptUiMessageIntent
    object LoginRequired : CreateReceiptUiMessageIntent
    class AttemptsLeft(val attemptsLeft: Int) : CreateReceiptUiMessageIntent
}

interface CreateReceiptIntent : BasicIntent {
    class NewReceiptIsCreated(val receiptId: Long) : CreateReceiptIntent
    object UserIsEmpty : CreateReceiptIntent
}

enum class CreateReceiptUiMessage(val message: String) {
    INTERNAL_ERROR("An internal error has occurred."),
    NETWORK_ERROR("A network error (such as timeout, interrupted connection or unreachable host) has occurred."),
    IMAGE_IS_INAPPROPRIATE("Image is inappropriate. Choose another one."),
    TOO_MANY_ATTEMPTS("Too many attempts. Try again later."),
    LOGIN_REQUIRED("Login required."),
    ATTEMPTS_LEFT("You have attempts left."),
}