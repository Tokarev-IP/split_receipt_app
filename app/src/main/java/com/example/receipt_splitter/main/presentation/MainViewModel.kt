package com.example.receipt_splitter.main.presentation

import com.example.receipt_splitter.main.basic.BasicViewModel

class MainViewModel :
    BasicViewModel<MainUiState, MainIntent, MainUiEvent, MainUiErrorIntent>(
        initialUiState = MainUiState.Show
    ) {
    private var userId: String? = null

    override fun setUiEvent(newUiEvent: MainUiEvent) {
        when (newUiEvent) {
            is MainUiEvent.UserIsSignedIn -> {
                userId = newUiEvent.userId
                setIntent(MainIntent.GoToReceiptScreen)
            }

            is MainUiEvent.UserIsSignedOut -> {
                setIntent(MainIntent.GoToLoginScreen)
            }

        }
    }
}