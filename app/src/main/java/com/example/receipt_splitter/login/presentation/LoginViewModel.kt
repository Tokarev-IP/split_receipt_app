package com.example.receipt_splitter.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.login.domain.SignInUseCaseInterface
import com.example.receipt_splitter.login.domain.SignInUseCaseResponse
import com.example.receipt_splitter.main.basic.BasicViewModel
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCaseInterface,
) : BasicViewModel<LoginUiState, LoginIntent, LoginUiEvent, LoginUiErrorIntent>(
    initialUiState = LoginUiState.Show
) {

    override fun setUiEvent(newUiEvent: LoginUiEvent) {

        when (newUiEvent) {
            is LoginUiEvent.LoginWithGoogleWasClicked -> {
                setUiState(LoginUiState.Loading)
                setIntent(LoginIntent.ShowGoogleSignInPopUp)
            }

            is LoginUiEvent.GoogleAuthCredentialWasChosen -> {
                setUiState(LoginUiState.Loading)
                newUiEvent.authCredential?.let {
                    signInWithCredential(authCredential = it)
                } ?: setUiState(LoginUiState.Show)
            }

            is LoginUiEvent.CheckIfUserIsSignedIn -> {
                setUiState(LoginUiState.Loading)
                checkCurrentUserId()
            }

            is LoginUiEvent.SetLoadingState -> {
                setUiState(LoginUiState.Loading)
            }

            is LoginUiEvent.SetShowState -> {
                setUiState(LoginUiState.Show)
            }
        }
    }

    private fun signInWithCredential(authCredential: AuthCredential) {
        viewModelScope.launch {
            val response = signInUseCase.signInWithAuthCredential(authCredential = authCredential)
            handleSignInResponse(response)
        }
    }

    private fun checkCurrentUserId() {
        viewModelScope.launch {
            val response = signInUseCase.getCurrentUserId()
            handleSignInResponse(response)
        }
    }

    private fun handleSignInResponse(response: SignInUseCaseResponse) {
        when (response) {
            is SignInUseCaseResponse.UserId -> {
                setIntent(LoginIntent.UserHasSignedIn(userId = response.userId))
            }

            is SignInUseCaseResponse.UserIsNull -> {
                setIntent(LoginIntent.UserHasToSignIn)
            }

            is SignInUseCaseResponse.Error -> {
                setUiState(LoginUiState.Show)
                setUiErrorIntent(LoginUiErrorIntent.UiError(msg = response.msg))
            }
        }
    }

}