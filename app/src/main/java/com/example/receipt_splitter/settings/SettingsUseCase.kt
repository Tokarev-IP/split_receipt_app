package com.example.receipt_splitter.settings

import com.example.receipt_splitter.login.data.FirebaseAuthenticationInterface
import com.google.firebase.auth.FirebaseUser

class SettingsUseCase(
    private val firebaseAuthentication: FirebaseAuthenticationInterface
) : SettingsUseCaseInterface {

    override suspend fun signOut() {
        firebaseAuthentication.signOut()
    }

    override suspend fun getUserData(): FirebaseUser? {
        return firebaseAuthentication.getCurrentUser()
    }
}

interface SettingsUseCaseInterface {
    suspend fun signOut()
    suspend fun getUserData(): FirebaseUser?
}