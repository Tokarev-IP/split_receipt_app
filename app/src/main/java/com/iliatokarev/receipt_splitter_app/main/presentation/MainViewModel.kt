package com.iliatokarev.receipt_splitter_app.main.presentation

import com.iliatokarev.receipt_splitter_app.main.basic.BasicViewModel

class MainViewModel :
    BasicViewModel<MainUiState, MainIntent, MainEvent, MainUiMessageIntent>(
        initialUiState = MainUiState.Show
    ) {

    override fun setEvent(newEvent: MainEvent) {
        when (newEvent) {
            is MainEvent.UserIsSignedIn -> {
                setIntent(MainIntent.GoToReceiptScreen)
            }

            is MainEvent.UserIsSignedOut -> {
                setIntent(MainIntent.GoToLoginScreen)
            }

            is MainEvent.OpenSettings -> {
                setIntent(MainIntent.GoToSettingsScreen)
            }

            is MainEvent.GoBackNavigation -> {
                setIntent(MainIntent.GoBackNavigation)
            }
        }
    }
}