package com.example.receipt_splitter.receipt.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicNavigationEvent
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.main.basic.BasicViewModel
import com.example.receipt_splitter.receipt.domain.usecases.CreateReceiptUseCaseInterface
import com.example.receipt_splitter.receipt.domain.usecases.ReceiptCreationResult
import com.example.receipt_splitter.receipt.presentation.ReceiptUiMessage
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
                    createReceipt(listOfImages = it)
                } ?: setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            }

            is CreateReceiptEvent.PutImages -> {
                putReceiptImages(listOfImages = newEvent.listOfImages)
            }
        }
    }

    private fun createReceipt(listOfImages: List<Uri>) {
        viewModelScope.launch {
            setUiState(CreateReceiptUiState.Loading)
            val response =
                createReceiptUseCase.createReceiptFromUriImage(listOfImages = listOfImages)
            when (response) {
                is ReceiptCreationResult.Success -> {
                    setIntent(CreateReceiptIntent.GoToEditReceiptScreen(response.receiptId))
                }

                is ReceiptCreationResult.ImageIsInappropriate -> {
                    setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
                }

                is ReceiptCreationResult.Error -> {
                    setUiMessageIntent(handleReceiptCreationError(response.msg))
                }
            }
            setUiState(CreateReceiptUiState.Show)
        }
    }

    private fun putReceiptImages(listOfImages: List<Uri>) {
        viewModelScope.launch {
            val filteredListOfImages =
                createReceiptUseCase.filterBySize(listOfImages = listOfImages)
            val result: Boolean =
                createReceiptUseCase.areAllUriImagesAppropriate(listOfImages = listOfImages)
            if (result == false)
                setUiMessageIntent(CreateReceiptUiMessageIntent.SomeImagesAreInappropriate)
            setReceiptImages(filteredListOfImages)
        }
    }
}

private fun handleReceiptCreationError(errorMsg: String): CreateReceiptUiMessageIntent {
    when (errorMsg) {
        ReceiptUiMessage.INTERNAL_ERROR.msg -> CreateReceiptUiMessageIntent.InternalError
        ReceiptUiMessage.NETWORK_ERROR.msg -> CreateReceiptUiMessageIntent.InternetConnectionError
    }
    return CreateReceiptUiMessageIntent.InternalError
}

interface CreateReceiptUiState : BasicUiState {
    object Show : CreateReceiptUiState
    object Loading : CreateReceiptUiState
}

sealed interface CreateReceiptEvent : BasicEvent {
    object CreateReceipt : CreateReceiptEvent
    class PutImages(val listOfImages: List<Uri>) : CreateReceiptEvent
}

interface CreateReceiptUiMessageIntent : BasicUiMessageIntent {
    object SomeImagesAreInappropriate : CreateReceiptUiMessageIntent
    object InternalError : CreateReceiptUiMessageIntent
    object InternetConnectionError : CreateReceiptUiMessageIntent
}

interface CreateReceiptIntent : BasicIntent {
    class GoToEditReceiptScreen(val receiptId: Long) : CreateReceiptIntent
}