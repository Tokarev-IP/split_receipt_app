package com.example.receipt_splitter.settings

import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.main.basic.BasicEvent
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiMessageIntent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.example.receipt_splitter.main.basic.BasicViewModel
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsUseCase: SettingsUseCaseInterface,
) : BasicViewModel<
        SettingsUiState,
        SettingsIntent,
        SettingsEvent,
        SettingsUiMessageIntent,
        >(initialUiState = SettingsUiState.Show) {

    override fun setEvent(newEvent: SettingsEvent) {
        when (newEvent) {
            is SettingsEvent.SignOut -> {
                signOut()
            }

            is SettingsEvent.GoBackNavigation -> {
                setIntent(SettingsIntent.GoBackToReceipt)
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            settingsUseCase.signOut()
            setIntent(SettingsIntent.SignOut)
        }
    }

}

interface SettingsUiState : BasicUiState {
    object Show : SettingsUiState
}

interface SettingsIntent : BasicIntent {
    object SignOut : SettingsIntent
    object GoBackToReceipt : SettingsIntent
}
sealed interface SettingsEvent : BasicEvent {
    object SignOut : SettingsEvent
    object GoBackNavigation : SettingsEvent
}

interface SettingsUiMessageIntent : BasicUiMessageIntent {}