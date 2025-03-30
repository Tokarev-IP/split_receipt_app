package com.example.receipt_splitter.login.data

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialRequest.Builder
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class SignInWithGoogle {

    suspend fun signInWithGoogle(activity: Activity): AuthCredential? {
        return runCatching {
            if (activity.isDestroyed || activity.isFinishing) return null
            val googleRequest = getGoogleCredentialRequest()
            val result: GetCredentialResponse =
                CredentialManager.create(activity).getCredential(
                    context = activity,
                    request = googleRequest,
                )
            return handleSignIn(result)
        }
            .onFailure { return null }
            .getOrNull()
    }

    private fun getGoogleIdOption(): GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(DataConstantsLogin.SERVER_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    private fun getGoogleCredentialRequest(): GetCredentialRequest = Builder()
        .addCredentialOption(getGoogleIdOption())
        .build()

    private fun handleSignIn(result: GetCredentialResponse): AuthCredential? {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        null
                    )
                    return authCredential
                } else
                    return null
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                return null
            }

            else -> return null
        }
    }
}