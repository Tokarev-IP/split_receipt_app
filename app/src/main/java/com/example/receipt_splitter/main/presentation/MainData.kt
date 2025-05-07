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

    @Serializable
    object SettingsNav : MainNavHostDestinations
}

interface MainUiState : BasicUiState {
    object Loading : MainUiState
    object Show : MainUiState
}

interface MainIntent : BasicIntent {
    object GoToLoginScreen : MainIntent
    object GoToReceiptScreen : MainIntent
    object GoToSettingsScreen : MainIntent
    object GoBackNavigation : MainIntent
}

sealed interface MainEvent : BasicEvent {
    object UserIsSignedIn : MainEvent
    object UserIsSignedOut : MainEvent
    object OpenSettings : MainEvent
    object GoBackNavigation : MainEvent
}

sealed interface MainUiMessageIntent : BasicUiMessageIntent