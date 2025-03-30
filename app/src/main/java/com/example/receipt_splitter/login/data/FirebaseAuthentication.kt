package com.example.receipt_splitter.login.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthentication : FirebaseAuthenticationInterface {

    private val firebaseAuth = Firebase.auth

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    continuation.resume(result.user)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun signOut() {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signOut()
            continuation.resume(Unit)
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return suspendCancellableCoroutine { continuation ->
            continuation.resume(firebaseAuth.currentUser?.uid)
        }
    }
}

interface FirebaseAuthenticationInterface {
    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser?
    suspend fun signOut()
    suspend fun getCurrentUserId(): String?
}