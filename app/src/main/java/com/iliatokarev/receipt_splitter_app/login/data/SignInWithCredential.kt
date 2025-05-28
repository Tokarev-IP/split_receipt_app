package com.iliatokarev.receipt_splitter_app.login.data

import android.app.Activity
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialRequest.Builder
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiMessages

class SignInWithCredential {

    suspend fun signInWithGoogle(activity: Activity): AuthCredential {
        if (activity.isDestroyed || activity.isFinishing)
            return throw Exception(LoginUiMessages.INTERNAL_ERROR.message)
        val googleRequest = getGoogleCredentialRequest()
        val result: GetCredentialResponse =
            CredentialManager.create(activity).getCredential(
                context = activity,
                request = googleRequest,
            )
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
                    return throw Exception(LoginUiMessages.INTERNAL_ERROR.message)
            }

            else -> return throw Exception(LoginUiMessages.INTERNAL_ERROR.message)
        }
    }

    suspend fun signInWithSavedCredential(activity: Activity): Pair<String, String> {
        if (activity.isDestroyed || activity.isFinishing)
            return throw Exception(LoginUiMessages.INTERNAL_ERROR.message)
        val credentialRequest = getPasswordCredentialRequest()
        val result: GetCredentialResponse =
            CredentialManager.create(activity).getCredential(
                context = activity,
                request = credentialRequest,
            )

        when (val credential = result.credential) {
            is PasswordCredential -> {
                val email = credential.id
                val password = credential.password
                return Pair(email, password)
            }

            else -> return throw Exception(LoginUiMessages.INTERNAL_ERROR.message)
        }
    }

    private fun getGoogleIdOption(): GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(DataConstantsLogin.SERVER_CLIENT_ID)
        .setAutoSelectEnabled(false)
        .build()

    private fun getPasswordOption(): GetPasswordOption = GetPasswordOption()

    private fun getGoogleCredentialRequest(): GetCredentialRequest = Builder()
        .addCredentialOption(getGoogleIdOption())
        .build()

    private fun getPasswordCredentialRequest(): GetCredentialRequest = Builder()
        .addCredentialOption(getPasswordOption())
        .build()

    suspend fun registerPassword(
        username: String,
        password: String,
        activity: Activity,
    ): SaveEmailAndPasswordResponse {
        runCatching {
            val createPasswordRequest =
                CreatePasswordRequest(id = username, password = password)
            CredentialManager.create(activity).createCredential(
                context = activity,
                request = createPasswordRequest
            )
            return SaveEmailAndPasswordResponse.Success
        }.getOrElse { e: Throwable ->
            return if (e is CreateCredentialCancellationException) { //User has canceled credential saving
                SaveEmailAndPasswordResponse.CredentialCancellationException
            } else if (e is CreateCredentialUnknownException) { //Credential was canceled by user
                SaveEmailAndPasswordResponse.PasswordSavingHasBeenCanceledByUser
            } else {
                SaveEmailAndPasswordResponse.Error(
                    e.message ?: LoginUiMessages.INTERNAL_ERROR.message
                )
            }
        }
    }
}

sealed interface SaveEmailAndPasswordResponse {
    object Success : SaveEmailAndPasswordResponse
    object CredentialCancellationException : SaveEmailAndPasswordResponse
    object PasswordSavingHasBeenCanceledByUser : SaveEmailAndPasswordResponse
    class Error(val msg: String) : SaveEmailAndPasswordResponse
}