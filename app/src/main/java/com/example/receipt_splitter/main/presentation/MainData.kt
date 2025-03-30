package com.example.receipt_splitter.main.presentation

import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiErrorIntent
import com.example.receipt_splitter.main.basic.BasicUiEvent
import com.example.receipt_splitter.main.basic.BasicUiState
import kotlinx.serialization.Serializable

sealed interface MainNavHostDestinations {
    @Serializable
    object ReceiptNav : MainNavHostDestinations

    @Serializable
    object LoginNav : MainNavHostDestinations
}

interface MainUiState : BasicUiState {
    object Loading : MainUiState
    object Show : MainUiState
}

interface MainIntent : BasicIntent {
    object GoToLoginScreen : MainIntent
    object GoToReceiptScreen : MainIntent
}

sealed interface MainUiEvent : BasicUiEvent {
    class UserIsSignedIn(val userId: String) : MainUiEvent
    object UserIsSignedOut : MainUiEvent
}

sealed interface MainUiErrorIntent : BasicUiErrorIntent