package com.iliatokarev.receipt_splitter.login.presentation

import com.iliatokarev.receipt_splitter.main.basic.BasicEvent
import com.iliatokarev.receipt_splitter.main.basic.BasicIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicNavigationEvent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiMessageIntent
import com.iliatokarev.receipt_splitter.main.basic.BasicUiState
import com.google.firebase.auth.AuthCredential
import kotlinx.serialization.Serializable

interface LoginNavHostDestinations {

    @Serializable
    object SignInLoadingScreenNav : LoginNavHostDestinations

    @Serializable
    object ChooseSignInOptionScreenNav : LoginNavHostDestinations

    @Serializable
    object RegistrationScreenNav : LoginNavHostDestinations

    @Serializable
    class EmailVerificationScreenNav(val email: String, val password: String) :
        LoginNavHostDestinations

    @Serializable
    object ResetPasswordScreenNav : LoginNavHostDestinations
}

enum class LoginUiMessages(val message: String) {
    EMPTY_STRING("Given String is empty or null"),
    BAD_EMAIL_FORMAT("The email address is badly formatted."),
    INCORRECT_CREDENTIAL("The supplied auth credential is incorrect, malformed or has expired."),
    RESET_PASSWORD_EMAIL_WAS_SENT("Reset password email was sent"),
    VERIFICATION_EMAIL_WAS_SENT("Verification email was sent"),
    BLOCKED_ALL_REQUESTS("We have blocked all requests from this device due to unusual activity. Try again later."),
    EMAIL_IS_NOT_VERIFIED("Email is not verified"),
    EMAIL_ALREADY_IN_USE("The email address is already in use by another account."),
    INTERNAL_ERROR("An internal error has occurred."),
    NETWORK_ERROR("A network error (such as timeout, interrupted connection or unreachable host) has occurred."),
    ABSENT_OF_GOOGLE_ACCOUNT("Google account is absent"),
    NO_SAVED_ACCOUNTS("No saved accounts"),
    /*
    Full error:
    An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password may contain at most 50 characters] ]
    An internal error has occurred. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain at least 8 characters, Password must contain a numeric character] ]
    The given password is invalid. [ Password should be at least 6 characters ]
    */
    INVALID_PASSWORD("PASSWORD_DOES_NOT_MEET_REQUIREMENTS"),
}

interface LoginUiState : BasicUiState {
    object Loading : LoginUiState
    object Show : LoginUiState
}

interface LoginIntent : BasicIntent {
    object GoToSignInScreen : LoginIntent
    object UserIsAuthorized : LoginIntent
    object GoToRegistrationScreen : LoginIntent
    class GoToEmailVerificationScreen(val email: String, val password: String) : LoginIntent
    object GoNavigationBack : LoginIntent
    object GoToResetPasswordScreen : LoginIntent
    class ShowSaveCredentialsPopup(val email: String, val password: String) : LoginIntent
}

sealed interface LoginEvent : BasicEvent {
    class EmailAndPasswordLoginWasClicked(val email: String, val password: String) : LoginEvent
    class SavedEmailAndPasswordSignIn(val email: String, val password: String) : LoginEvent
    class GoogleAuthCredentialWasChosen(val authCredential: AuthCredential?) : LoginEvent
    data object CheckIfUserIsSignedIn : LoginEvent
    class SetErrorIntent(val msg: String) : LoginEvent
    object SendVerificationEmail : LoginEvent
    class SendResetPasswordRequest(val email: String) : LoginEvent
    class CreateNewAccount(val email: String, val password: String) : LoginEvent
    class ConfirmEmailVerification(val email: String, val password: String) : LoginEvent
    object SetShowState : LoginEvent
    object SetLoadingState : LoginEvent
    object SignOut : LoginEvent
}

sealed interface LoginNavigationEvent : BasicNavigationEvent {
    object GoNavigationBack : LoginNavigationEvent
    object RegistrationButtonWasClicked : LoginNavigationEvent
    object ForgotPasswordButtonWasClicked : LoginNavigationEvent
}

interface LoginUiMessageIntent : BasicUiMessageIntent {
    class ErrorMessage(val msg: String) : LoginUiMessageIntent
    object VerificationEmailWasSent : LoginUiMessageIntent
    object EmailIsNotVerified : LoginUiMessageIntent
    object ResetPasswordEmailWasSent : LoginUiMessageIntent
    object EmailOrPasswordIsWrong : LoginUiMessageIntent
    object EmailIsUsedByAnotherAccount : LoginUiMessageIntent
    object EmailIsInvalid : LoginUiMessageIntent
    object PasswordIsInvalid : LoginUiMessageIntent
    object TooManyRequests : LoginUiMessageIntent
    object InternalError : LoginUiMessageIntent
    object EmptyString : LoginUiMessageIntent
    object NoInternetConnection : LoginUiMessageIntent
    object AbsentOfGoogleAccount : LoginUiMessageIntent
    object NoSavedAccounts : LoginUiMessageIntent
}