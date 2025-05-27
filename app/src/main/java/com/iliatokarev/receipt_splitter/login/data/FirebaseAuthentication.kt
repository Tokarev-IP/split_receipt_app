package com.iliatokarev.receipt_splitter.login.data

import com.iliatokarev.receipt_splitter.login.presentation.LoginUiMessages
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthentication : FirebaseAuthenticationInterface {

    private val firebaseAuth = Firebase.auth

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    continuation.resume(result.user ?: throw Exception(LoginUiMessages.INTERNAL_ERROR.message))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    continuation.resume(result.user ?: throw Exception(LoginUiMessages.INTERNAL_ERROR.message))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    continuation.resume(result.user ?: throw Exception(LoginUiMessages.INTERNAL_ERROR.message))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun sendEmailVerification(currentUser: FirebaseUser) {
        return suspendCancellableCoroutine { continuation ->
            currentUser.sendEmailVerification()
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }
    }

    override suspend fun deleteUserAccount(currentUser: FirebaseUser) {
        return suspendCancellableCoroutine { continuation ->
            currentUser.delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
        }
    }
}

interface FirebaseAuthenticationInterface {
    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser
    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser
    suspend fun signOut()
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun sendEmailVerification(currentUser: FirebaseUser)
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun deleteUserAccount(currentUser: FirebaseUser)
}