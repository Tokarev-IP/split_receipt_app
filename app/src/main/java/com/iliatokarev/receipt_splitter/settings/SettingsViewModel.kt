package com.iliatokarev.receipt_splitter.settings

import androidx.lifecycle.viewModelScope
import com.iliatokarev.receipt_splitter.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiState
import com.iliatokarev.receipt_splitter.main.basic.BasicViewModel
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

            is SettingsEvent.DeleteUserAccount -> {
                deleteUserAccount()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            settingsUseCase.signOut()
            setIntent(SettingsIntent.SignOut)
        }
    }

    private fun deleteUserAccount() {
        viewModelScope.launch {
            setUiState(SettingsUiState.Loading)
            val response = settingsUseCase.deleteUserAccount()
            when (response) {
                is DeleteUserAccountResponse.Success -> {
                    setUiMessageIntent(SettingsUiMessageIntent.AccountWasDeleted)
                    setIntent(SettingsIntent.SignOut)
                }

                is DeleteUserAccountResponse.Error -> {
                    setUiMessageIntent(handleUiMessages(response.message))
                }

                is DeleteUserAccountResponse.EmptyUser -> {
                    setUiMessageIntent(SettingsUiMessageIntent.EmptyUser)
                    setIntent(SettingsIntent.SignOut)
                }
            }
            setUiState(SettingsUiState.Show)
        }
    }
}

private fun handleUiMessages(message: String): SettingsUiMessageIntent {
    return when (message) {
        SettingsUiMessages.INTERNAL_ERROR.message -> {
            SettingsUiMessageIntent.InternalError
        }

        else -> {
            SettingsUiMessageIntent.InternalError
        }
    }
}

interface SettingsUiState : BasicUiState {
    object Show : SettingsUiState
    object Loading : SettingsUiState
}

interface SettingsIntent : BasicIntent {
    object SignOut : SettingsIntent
    object GoBackToReceipt : SettingsIntent
}

sealed interface SettingsEvent : BasicEvent {
    object SignOut : SettingsEvent
    object GoBackNavigation : SettingsEvent
    object DeleteUserAccount : SettingsEvent
}

interface SettingsUiMessageIntent : BasicUiMessageIntent {
    object EmptyUser : SettingsUiMessageIntent
    object InternalError : SettingsUiMessageIntent
    object AccountWasDeleted : SettingsUiMessageIntent
}

enum class SettingsUiMessages(val message: String) {
    INTERNAL_ERROR("Internal error"),
    ACCOUNT_WAS_DELETED("Account was deleted"),
    USER_IS_EMPTY("User is empty"),
}