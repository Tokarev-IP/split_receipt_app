package com.iliatokarev.receipt_splitter_app.login.domain

import com.iliatokarev.receipt_splitter_app.login.data.FirebaseAuthenticationInterface
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiMessages
import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CurrentUserUseCase(
    private val firebaseAuthentication: FirebaseAuthenticationInterface
) : CurrentUserUseCaseInterface {

    private companion object {
        private const val DELAY_TIME = 1000L
        private const val LONG_DELAY_TIME = 3000L
    }

    override suspend fun getCurrentUser(): CurrentUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(DELAY_TIME)
                val user: FirebaseUser? = firebaseAuthentication.getCurrentUser()
                if (user != null) {
                    user.email?.let { userEmail ->
                        if (user.isEmailVerified)
                            return@withContext CurrentUseCaseResponse.EmailUser(user)
                        else {
                            firebaseAuthentication.signOut()
                            return@withContext CurrentUseCaseResponse.UserIsNull
                        }
                    } ?: return@withContext CurrentUseCaseResponse.GoogleSignInUser(user)
                } else
                    return@withContext CurrentUseCaseResponse.UserIsNull
            }.getOrElse { e: Throwable ->
                return@withContext CurrentUseCaseResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun sendEmailVerification(currentUser: FirebaseUser): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(LONG_DELAY_TIME)
                firebaseAuthentication.sendEmailVerification(currentUser = currentUser)
                return@withContext BasicFunResponse.Success
            }.getOrElse { e: Throwable ->
                return@withContext BasicFunResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun sendResetPasswordRequest(email: String): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(LONG_DELAY_TIME)
                firebaseAuthentication.sendPasswordResetEmail(email = email)
                return@withContext BasicFunResponse.Success
            }.getOrElse { e: Throwable ->
                return@withContext BasicFunResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun signOut(): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                firebaseAuthentication.signOut()
                return@withContext BasicFunResponse.Success
            }.getOrElse { e: Throwable ->
                return@withContext BasicFunResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun isEmailVerified(
        email: String,
        password: String,
    ): EmailVerifiedResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(LONG_DELAY_TIME)
                val user = firebaseAuthentication.signInWithEmailAndPassword(
                    email = email,
                    password = password
                )
                if (user.isEmailVerified)
                    return@withContext EmailVerifiedResponse.EmailIsVerified
                else
                    return@withContext EmailVerifiedResponse.EmailIsNotVerified
            }.getOrElse { e: Throwable ->
                return@withContext EmailVerifiedResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }
}

interface CurrentUserUseCaseInterface {
    suspend fun getCurrentUser(): CurrentUseCaseResponse
    suspend fun sendEmailVerification(currentUser: FirebaseUser): BasicFunResponse
    suspend fun sendResetPasswordRequest(email: String): BasicFunResponse
    suspend fun signOut(): BasicFunResponse
    suspend fun isEmailVerified(email: String, password: String): EmailVerifiedResponse
}

sealed interface CurrentUseCaseResponse {
    class EmailUser(val currentUser: FirebaseUser) : CurrentUseCaseResponse
    class GoogleSignInUser(val currentUser: FirebaseUser) : CurrentUseCaseResponse
    object UserIsNull : CurrentUseCaseResponse
    class Error(val msg: String) : CurrentUseCaseResponse
}

sealed interface EmailVerifiedResponse {
    object EmailIsVerified : EmailVerifiedResponse
    object EmailIsNotVerified : EmailVerifiedResponse
    class Error(val msg: String) : EmailVerifiedResponse
}