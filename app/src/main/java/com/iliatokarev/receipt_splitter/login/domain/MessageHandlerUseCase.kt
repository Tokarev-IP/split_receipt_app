package com.iliatokarev.receipt_splitter.login.domain

import com.iliatokarev.receipt_splitter.login.presentation.LoginUiMessageIntent
import com.iliatokarev.receipt_splitter.login.presentation.LoginUiMessages

class MessageHandlerUseCase : MessageHandlerUseCaseInterface {
    override fun handlerUiMessage(message: String): LoginUiMessageIntent {
        when (message) {
            LoginUiMessages.EMAIL_ALREADY_IN_USE.message -> {
                return LoginUiMessageIntent.EmailIsUsedByAnotherAccount
            }

            LoginUiMessages.BAD_EMAIL_FORMAT.message -> {
                return LoginUiMessageIntent.EmailIsInvalid
            }

            LoginUiMessages.EMAIL_IS_NOT_VERIFIED.message -> {
                return LoginUiMessageIntent.EmailIsNotVerified
            }

            LoginUiMessages.INTERNAL_ERROR.message -> {
                return LoginUiMessageIntent.InternalError
            }

            in LoginUiMessages.BLOCKED_ALL_REQUESTS.message -> {
                return LoginUiMessageIntent.TooManyRequests
            }

            LoginUiMessages.RESET_PASSWORD_EMAIL_WAS_SENT.message -> {
                return LoginUiMessageIntent.ResetPasswordEmailWasSent
            }

            LoginUiMessages.VERIFICATION_EMAIL_WAS_SENT.message -> {
                return LoginUiMessageIntent.VerificationEmailWasSent
            }

            LoginUiMessages.EMPTY_STRING.message -> {
                return LoginUiMessageIntent.EmptyString
            }

            LoginUiMessages.INCORRECT_CREDENTIAL.message -> {
                return LoginUiMessageIntent.EmailOrPasswordIsWrong
            }

            LoginUiMessages.NETWORK_ERROR.message -> {
                return LoginUiMessageIntent.NoInternetConnection
            }

            LoginUiMessages.ABSENT_OF_GOOGLE_ACCOUNT.message -> {
                return LoginUiMessageIntent.AbsentOfGoogleAccount
            }

            LoginUiMessages.NO_SAVED_ACCOUNTS.message -> {
                return LoginUiMessageIntent.NoSavedAccounts
            }

            else -> {
                return if (LoginUiMessages.INVALID_PASSWORD.message in message)
                    LoginUiMessageIntent.PasswordIsInvalid
                else
                    LoginUiMessageIntent.ErrorMessage(msg = message)
            }
        }
    }
}

interface MessageHandlerUseCaseInterface {
    fun handlerUiMessage(text: String): LoginUiMessageIntent
}