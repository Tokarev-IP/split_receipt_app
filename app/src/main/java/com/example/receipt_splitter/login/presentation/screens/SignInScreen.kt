package com.example.receipt_splitter.login.presentation.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.receipt_splitter.R
import com.example.receipt_splitter.login.data.SignInWithCredential
import com.example.receipt_splitter.login.presentation.LoginEvent
import com.example.receipt_splitter.login.presentation.LoginNavigationEvent
import com.example.receipt_splitter.login.presentation.LoginUiMessageIntent
import com.example.receipt_splitter.login.presentation.LoginUiMessages
import com.example.receipt_splitter.login.presentation.LoginUiState
import com.example.receipt_splitter.login.presentation.LoginViewModel
import com.example.receipt_splitter.login.presentation.views.SignInScreenView
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    localActivity: Activity? = LocalActivity.current
) {
    val uiState by loginViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var signInTextFieldErrorState by remember {
        mutableStateOf<SignInTextFieldErrorState>(
            SignInTextFieldErrorState()
        )
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow().collect { uiMessageIntent ->
            uiMessageIntent?.let {
                loginViewModel.clearUiMessageIntentFlow()
                handleUiMessages(
                    uiMessageIntent,
                    onSignInTextFieldErrorState = { state -> signInTextFieldErrorState = state }
                )
            }
        }
    }

    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(key1 = isFocused) {
        if (isFocused && emailText.isEmpty()) {
            localActivity?.let { myActivity ->
                showCredentialSignInPopUp(
                    myActivity,
                    loginViewModel,
                    onCredentialResponse = { email, password ->
                        emailText = email
                        passwordText = password
                        signInTextFieldErrorState = SignInTextFieldErrorState()
                    }
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.sign_in))
                }
            )
        },
    ) { innerPadding ->
        when (uiState) {
            is LoginUiState.Loading -> {
                LinearProgressIndicator(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                )
            }
        }

        SignInScreenView(
            modifier = modifier.padding(innerPadding),
            onEmailAndPasswordSignInClick = { email, password ->
                signInTextFieldErrorState = SignInTextFieldErrorState()
                loginViewModel.setEvent(
                    LoginEvent.EmailAndPasswordLoginWasClicked(
                        email,
                        password
                    )
                )
            },
            onGoogleSignInClick = {
                signInTextFieldErrorState = SignInTextFieldErrorState()
                localActivity?.let { myActivity ->
                    coroutineScope.launch {
                        showGoogleSignInPopUp(
                            myActivity,
                            loginViewModel,
                        )
                    }
                }
            },
            onRegistrationClick = {
                signInTextFieldErrorState = SignInTextFieldErrorState()
                loginViewModel.setNavigationEvent(LoginNavigationEvent.RegistrationButtonWasClicked)
            },
            onForgotPasswordClick = {
                signInTextFieldErrorState = SignInTextFieldErrorState()
                loginViewModel.setNavigationEvent(LoginNavigationEvent.ForgotPasswordButtonWasClicked)
            },
            enabled = { uiState is LoginUiState.Show },
            textFieldErrors = { signInTextFieldErrorState },
            onTextFieldErrorStateChanged = { state: SignInTextFieldErrorState ->
                signInTextFieldErrorState = state
            },
            emailText = { emailText },
            passwordText = { passwordText },
            onEmailTextChanged = { value: String ->
                emailText = value
            },
            onPasswordTextChanged = { value: String ->
                passwordText = value
            },
            interactionSource = { interactionSource },
        )
    }
}

internal class SignInTextFieldErrorState(
    val emailIsInvalid: Boolean = false,
    val emailIsIncorrect: Boolean = false,
    val passwordIsInvalid: Boolean = false,
    val passwordIsIncorrect: Boolean = false,
)

private suspend fun showCredentialSignInPopUp(
    myActivity: Activity,
    loginViewModel: LoginViewModel,
    onCredentialResponse: (email: String, password: String) -> Unit,
) {
    runCatching {
        val credentials: Pair<String, String> =
            SignInWithCredential().signInWithSavedCredential(myActivity)
        onCredentialResponse(credentials.first, credentials.second)
        loginViewModel.setEvent(
            LoginEvent.SavedEmailAndPasswordSignIn(
                email = credentials.first,
                password = credentials.second
            )
        )
    }.onFailure { e: Throwable ->
//        loginViewModel.setEvent(LoginEvent.SetErrorIntent(msg = LoginUiMessages.INTERNAL_ERROR.message))
    }
}

private suspend fun showGoogleSignInPopUp(
    myActivity: Activity,
    loginViewModel: LoginViewModel,
) {
    runCatching {
        val authCredential: AuthCredential =
            SignInWithCredential().signInWithGoogle(myActivity)
        loginViewModel.setEvent(
            LoginEvent.GoogleAuthCredentialWasChosen(authCredential)
        )
    }.onFailure { e: Throwable ->
//        loginViewModel.setEvent(LoginEvent.SetErrorIntent(msg = LoginUiMessages.INTERNAL_ERROR.message))
    }
}

private fun handleUiMessages(
    uiMessageIntent: LoginUiMessageIntent,
    onSignInTextFieldErrorState: (SignInTextFieldErrorState) -> Unit,
) {
    when (uiMessageIntent) {
        is LoginUiMessageIntent.EmailIsInvalid -> {
            onSignInTextFieldErrorState(
                SignInTextFieldErrorState(emailIsInvalid = true)
            )
        }

        is LoginUiMessageIntent.PasswordIsInvalid -> {
            onSignInTextFieldErrorState(
                SignInTextFieldErrorState(passwordIsInvalid = true)
            )
        }

        is LoginUiMessageIntent.EmailOrPasswordIsWrong -> {
            onSignInTextFieldErrorState(
                SignInTextFieldErrorState(
                    emailIsIncorrect = true,
                    passwordIsIncorrect = true
                )
            )
        }

    }
}