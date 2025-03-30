package com.example.receipt_splitter.login.presentation

import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiErrorIntent
import com.example.receipt_splitter.main.basic.BasicUiEvent
import com.example.receipt_splitter.main.basic.BasicUiState
import com.google.firebase.auth.AuthCredential
import kotlinx.serialization.Serializable

interface LoginNavHostDestinations {

    @Serializable
    object SignInLoadingScreenNav : LoginNavHostDestinations

    @Serializable
    object ChooseSignInOptionScreenNav : LoginNavHostDestinations
}

interface LoginUiState : BasicUiState {
    object Loading : LoginUiState
    object Show : LoginUiState
}

interface LoginIntent : BasicIntent {
    object UserHasToSignIn : LoginIntent
    object ShowGoogleSignInPopUp : LoginIntent
    class UserHasSignedIn(val userId: String) : LoginIntent
}

sealed interface LoginUiEvent : BasicUiEvent {
    data object LoginWithGoogleWasClicked : LoginUiEvent
    class GoogleAuthCredentialWasChosen(val authCredential: AuthCredential?) : LoginUiEvent
    data object CheckIfUserIsSignedIn : LoginUiEvent
    data object SetShowState : LoginUiEvent
    data object SetLoadingState : LoginUiEvent
}

interface LoginUiErrorIntent: BasicUiErrorIntent {
    class UiError(val msg: String) : LoginUiErrorIntent
}