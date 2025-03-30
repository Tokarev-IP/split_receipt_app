package com.example.receipt_splitter.login.domain

import com.example.receipt_splitter.login.data.FirebaseAuthenticationInterface
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SignInUseCase(
    private val firebaseAuthentication: FirebaseAuthenticationInterface,
) : SignInUseCaseInterface {

    override suspend fun signInWithAuthCredential(
        authCredential: AuthCredential
    ): SignInUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val user: FirebaseUser? =
                    firebaseAuthentication.signInWithCredential(credential = authCredential)
                delay(1000)
                if (user != null)
                    return@withContext SignInUseCaseResponse.UserId(user.uid)
                else
                    return@withContext SignInUseCaseResponse.UserIsNull
            }.getOrElse { e: Throwable ->
                return@withContext SignInUseCaseResponse.Error(e.message ?: "Some errors")
            }
        }
    }

    override suspend fun getCurrentUserId(): SignInUseCaseResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val userId: String? = firebaseAuthentication.getCurrentUserId()
                delay(1000)
                if (userId != null)
                    return@withContext SignInUseCaseResponse.UserId(userId)
                else
                    return@withContext SignInUseCaseResponse.UserIsNull
            }.getOrElse { e: Throwable ->
                return@withContext SignInUseCaseResponse.Error(e.message ?: "Some errors")
            }
        }
    }
}

interface SignInUseCaseInterface {
    suspend fun signInWithAuthCredential(authCredential: AuthCredential): SignInUseCaseResponse

    suspend fun getCurrentUserId(): SignInUseCaseResponse
}

sealed interface SignInUseCaseResponse {
    class UserId(val userId: String) : SignInUseCaseResponse
    object UserIsNull : SignInUseCaseResponse
    class Error(val msg: String) : SignInUseCaseResponse
}