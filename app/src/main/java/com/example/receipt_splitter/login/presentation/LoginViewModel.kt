package com.example.receipt_splitter.login.presentation

import androidx.lifecycle.viewModelScope
import com.example.receipt_splitter.login.domain.CurrentUseCaseResponse
import com.example.receipt_splitter.login.domain.CurrentUserUseCaseInterface
import com.example.receipt_splitter.login.domain.EmailVerifiedResponse
import com.example.receipt_splitter.login.domain.MessageHandlerUseCaseInterface
import com.example.receipt_splitter.login.domain.SignInUseCaseInterface
import com.example.receipt_splitter.login.domain.SignInUseCaseResponse
import com.example.receipt_splitter.main.basic.BasicFunResponse
import com.example.receipt_splitter.main.basic.BasicLoginViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCaseInterface,
    private val currentUserUseCase: CurrentUserUseCaseInterface,
    private val messageHandlerUseCase: MessageHandlerUseCaseInterface,
) : BasicLoginViewModel<LoginUiState, LoginIntent, LoginEvent, LoginNavigationEvent, LoginUiMessageIntent>(
    initialUiState = LoginUiState.Show
) {
    private var firebaseUser: FirebaseUser? = null

    override fun setEvent(newEvent: LoginEvent) {
        when (newEvent) {
            is LoginEvent.GoogleAuthCredentialWasChosen -> {
                newEvent.authCredential?.let {
                    signInWithCredential(authCredential = it)
                } ?: setUiState(LoginUiState.Show)
            }

            is LoginEvent.CheckIfUserIsSignedIn -> {
                checkCurrentUser()
            }

            is LoginEvent.EmailAndPasswordLoginWasClicked -> {
                signInWithEmailAndPassword(
                    email = newEvent.email,
                    password = newEvent.password
                )
            }

            is LoginEvent.SavedEmailAndPasswordSignIn -> {
                signInWithSavedEmailAndPassword(
                    email = newEvent.email,
                    password = newEvent.password
                )
            }

            is LoginEvent.SendVerificationEmail -> {
                firebaseUser?.let { currentUser ->
                    sendVerificationEmail(currentUser)
                } ?: setIntent(LoginIntent.GoToSignInScreen)
            }

            is LoginEvent.SendResetPasswordRequest -> {
                sendResetPasswordRequest(newEvent.email)
            }

            is LoginEvent.CreateNewAccount -> {
                createNewUserAccount(
                    email = newEvent.email,
                    password = newEvent.password
                )
            }

            is LoginEvent.ConfirmEmailVerification -> {
                confirmEmailVerification(
                    email = newEvent.email,
                    password = newEvent.password
                )
            }

            is LoginEvent.SetShowState -> {
                setUiState(LoginUiState.Show)
            }

            is LoginEvent.SetLoadingState -> {
                setUiState(LoginUiState.Loading)
            }

            is LoginEvent.SetErrorIntent -> {
                setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = newEvent.msg))
            }

            is LoginEvent.SignOut -> {
                signOut()
            }
        }
    }

    override fun setNavigationEvent(newNavigationEvent: LoginNavigationEvent) {
        when (newNavigationEvent) {
            is LoginNavigationEvent.GoNavigationBack -> {
                setIntent(LoginIntent.GoNavigationBack)
            }

            is LoginNavigationEvent.ForgotPasswordButtonWasClicked -> {
                setIntent(LoginIntent.GoToResetPasswordScreen)
            }

            is LoginNavigationEvent.RegistrationButtonWasClicked -> {
                setIntent(LoginIntent.GoToRegistrationScreen)
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val response: CurrentUseCaseResponse = currentUserUseCase.getCurrentUser()
            when (response) {
                is CurrentUseCaseResponse.EmailUser -> {
                    firebaseUser = response.currentUser
                    setIntent(LoginIntent.UserIsAuthorized)
                }

                is CurrentUseCaseResponse.GoogleSignInUser -> {
                    firebaseUser = response.currentUser
                    setIntent(LoginIntent.UserIsAuthorized)
                }

                is CurrentUseCaseResponse.UserIsNull -> {
                    setIntent(LoginIntent.GoToSignInScreen)
                }

                is CurrentUseCaseResponse.Error -> {
                    setIntent(LoginIntent.GoToSignInScreen)
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: SignInUseCaseResponse =
                signInUseCase.signInWithEmailAndPassword(email = email, password = password)
            when (response) {
                is SignInUseCaseResponse.User -> {
                    firebaseUser = response.currentUser
                    setIntent(
                        LoginIntent.ShowSaveCredentialsPopup(
                            email = email,
                            password = password
                        )
                    )
                    if (response.currentUser.isEmailVerified) {
                        setIntent(LoginIntent.UserIsAuthorized)
                    } else {
                        setIntent(
                            LoginIntent.GoToEmailVerificationScreen(
                                email = email,
                                password = password
                            )
                        )
                    }
                }

                is SignInUseCaseResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun signInWithSavedEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: SignInUseCaseResponse =
                signInUseCase.signInWithEmailAndPassword(email = email, password = password)
            when (response) {
                is SignInUseCaseResponse.User -> {
                    firebaseUser = response.currentUser
                    if (response.currentUser.isEmailVerified) {
                        setIntent(LoginIntent.UserIsAuthorized)
                    } else {
                        setIntent(
                            LoginIntent.GoToEmailVerificationScreen(
                                email = email,
                                password = password
                            )
                        )
                    }
                }

                is SignInUseCaseResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun signInWithCredential(authCredential: AuthCredential) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: SignInUseCaseResponse =
                signInUseCase.signInWithAuthCredential(authCredential = authCredential)
            when (response) {
                is SignInUseCaseResponse.User -> {
                    firebaseUser = response.currentUser
                    setIntent(LoginIntent.UserIsAuthorized)
                }

                is SignInUseCaseResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun createNewUserAccount(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: SignInUseCaseResponse =
                signInUseCase.createAccount(email = email, password = password)
            when (response) {
                is SignInUseCaseResponse.User -> {
                    firebaseUser = response.currentUser
                    setIntent(
                        LoginIntent.ShowSaveCredentialsPopup(email = email, password = password)
                    )
                    setIntent(
                        LoginIntent.GoToEmailVerificationScreen(email = email, password = password)
                    )
                }

                is SignInUseCaseResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun sendVerificationEmail(currentUser: FirebaseUser) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: BasicFunResponse = currentUserUseCase.sendEmailVerification(currentUser)
            when (response) {
                is BasicFunResponse.Success -> {
                    setUiMessageIntent(LoginUiMessageIntent.VerificationEmailWasSent)
                }

                is BasicFunResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun sendResetPasswordRequest(email: String) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response: BasicFunResponse =
                currentUserUseCase.sendResetPasswordRequest(email = email)
            when (response) {
                is BasicFunResponse.Success -> {
                    setUiMessageIntent(LoginUiMessageIntent.ResetPasswordEmailWasSent)
                }

                is BasicFunResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            currentUserUseCase.signOut()
        }
    }

    private fun confirmEmailVerification(email: String, password: String) {
        viewModelScope.launch {
            setUiState(LoginUiState.Loading)
            val response = currentUserUseCase.isEmailVerified(email = email, password = password)
            when (response) {
                is EmailVerifiedResponse.EmailIsVerified -> {
                    setIntent(LoginIntent.UserIsAuthorized)
                }

                is EmailVerifiedResponse.EmailIsNotVerified -> {
                    setUiMessageIntent(LoginUiMessageIntent.EmailIsNotVerified)
                }

                is EmailVerifiedResponse.Error -> {
                    setUiMessageIntent(messageHandlerUseCase.handlerUiMessage(text = response.msg))
                }
            }
            setUiState(LoginUiState.Show)
        }
    }
}