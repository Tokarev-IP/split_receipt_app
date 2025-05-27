package com.iliatokarev.receipt_splitter.login.domain

import com.iliatokarev.receipt_splitter.login.data.FirebaseAuthenticationInterface
import com.iliatokarev.receipt_splitter.login.presentation.LoginUiMessages
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SignInUseCase(
    private val firebaseAuthentication: FirebaseAuthenticationInterface,
) : SignInUseCaseInterface {

    private companion object {
        private const val DELAY_TIME = 1000L
    }

    override suspend fun signInWithAuthCredential(
        authCredential: AuthCredential
    ): SignInUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(DELAY_TIME)
                val user: FirebaseUser =
                    firebaseAuthentication.signInWithCredential(credential = authCredential)
                return@withContext SignInUseCaseResponse.User(user)
            }.getOrElse { e: Throwable ->
                return@withContext SignInUseCaseResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): SignInUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(DELAY_TIME)
                val user: FirebaseUser =
                    firebaseAuthentication.signInWithEmailAndPassword(email, password)
                if (!user.isEmailVerified)
                    firebaseAuthentication.sendEmailVerification(user)
                return@withContext SignInUseCaseResponse.User(user)
            }.getOrElse { e: Throwable ->
                return@withContext SignInUseCaseResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }

    override suspend fun createAccount(
        email: String,
        password: String
    ): SignInUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                delay(DELAY_TIME)
                val user: FirebaseUser =
                    firebaseAuthentication.createUserWithEmailAndPassword(email, password)
                firebaseAuthentication.sendEmailVerification(user)
                return@withContext SignInUseCaseResponse.User(user)
            }.getOrElse { e: Throwable ->
                return@withContext SignInUseCaseResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }
}

interface SignInUseCaseInterface {
    suspend fun signInWithAuthCredential(authCredential: AuthCredential): SignInUseCaseResponse
    suspend fun signInWithEmailAndPassword(email: String, password: String): SignInUseCaseResponse
    suspend fun createAccount(email: String, password: String): SignInUseCaseResponse
}

sealed interface SignInUseCaseResponse {
    class User(val currentUser: FirebaseUser) : SignInUseCaseResponse
    class Error(val msg: String) : SignInUseCaseResponse
}