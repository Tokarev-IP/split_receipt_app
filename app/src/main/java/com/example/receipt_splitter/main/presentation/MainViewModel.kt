package com.example.receipt_splitter.main.presentation

import com.example.receipt_splitter.main.basic.BasicViewModel

class MainViewModel :
    BasicViewModel<MainUiState, MainIntent, MainEvent, MainNavigationEvent, MainUiMessageIntent>(
        initialUiState = MainUiState.Show
    ) {

    override fun setUiEvent(newUiEvent: MainEvent) {
        when (newUiEvent) {
            is MainEvent.UserIsSignedIn -> {
                setIntent(MainIntent.GoToReceiptScreen)
            }

            is MainEvent.UserIsSignedOut -> {
                setIntent(MainIntent.GoToLoginScreen)
            }

        }
    }

    override fun setNavigationEvent(newNavigationEvent: MainNavigationEvent) {
        TODO("Not yet implemented")
    }
}