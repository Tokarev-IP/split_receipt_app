package com.example.receipt_splitter.main.presentation

import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicNavigationEvent
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
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

sealed interface MainEvent : BasicEvent {
    object UserIsSignedIn : MainEvent
    object UserIsSignedOut : MainEvent
}

sealed interface MainNavigationEvent : BasicNavigationEvent

sealed interface MainUiMessageIntent : BasicUiMessageIntent