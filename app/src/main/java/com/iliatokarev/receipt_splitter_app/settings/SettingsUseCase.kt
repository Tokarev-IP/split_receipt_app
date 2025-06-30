package com.iliatokarev.receipt_splitter_app.settings

import com.iliatokarev.receipt_splitter_app.login.data.FirebaseAuthenticationInterface
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.iliatokarev.receipt_splitter_app.receipt.data.store.FireStoreRepositoryInterface

class SettingsUseCase(
    private val firebaseAuthentication: FirebaseAuthenticationInterface,
    private val fireStoreRepository: FireStoreRepositoryInterface,
) : SettingsUseCaseInterface {

    override suspend fun signOut() {
        firebaseAuthentication.signOut()
    }

    override suspend fun getUserData(): FirebaseUser? {
        return firebaseAuthentication.getCurrentUser()
    }

    override suspend fun deleteUserAccount(): DeleteUserAccountResponse {
        runCatching {
            val currentUser = firebaseAuthentication.getCurrentUser()
            currentUser?.let { user ->
                fireStoreRepository.deleteUserAttemptsData(documentId = user.uid)
                firebaseAuthentication.deleteUserAccount(currentUser = currentUser)
            } ?: return DeleteUserAccountResponse.EmptyUser
            return DeleteUserAccountResponse.Success
        }.getOrElse { e: Throwable ->
            return if (e is FirebaseAuthRecentLoginRequiredException)
                DeleteUserAccountResponse.EmptyUser
            else
                DeleteUserAccountResponse.Error(
                    e.message ?: SettingsUiMessages.INTERNAL_ERROR.message
                )
        }
    }
}

interface SettingsUseCaseInterface {
    suspend fun signOut()
    suspend fun getUserData(): FirebaseUser?
    suspend fun deleteUserAccount(): DeleteUserAccountResponse
}

interface DeleteUserAccountResponse {
    object Success : DeleteUserAccountResponse
    class Error(val message: String) : DeleteUserAccountResponse
    object EmptyUser : DeleteUserAccountResponse
}