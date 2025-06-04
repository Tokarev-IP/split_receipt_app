package com.iliatokarev.receipt_splitter_app.login.presentation.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iliatokarev.receipt_splitter_app.R
import com.iliatokarev.receipt_splitter_app.login.data.SignInWithCredential
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginNavigationEvent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiMessageIntent
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiMessages
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginUiState
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginViewModel
import com.iliatokarev.receipt_splitter_app.login.presentation.views.SignInScreenView
import com.google.firebase.auth.AuthCredential
import com.iliatokarev.receipt_splitter_app.main.basic.getAppVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    localActivity: Activity? = LocalActivity.current,
    localContext: Context = LocalContext.current,
) {
    val appVersion = stringResource(R.string.version_of_app, getAppVersion(localContext))
    val uiState by loginViewModel.getUiStateFlow().collectAsStateWithLifecycle()

    var signInTextFieldErrorState by remember {
        mutableStateOf<SignInTextFieldErrorState>(
            SignInTextFieldErrorState()
        )
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow()
            .debounce(500L)
            .collectLatest { uiMessageIntent ->
                handleUiMessages(
                    uiMessageIntent = uiMessageIntent,
                    onSignInTextFieldErrorState = { state -> signInTextFieldErrorState = state }
                )
            }
    }

    var emailText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    var showSavedCredential by remember { mutableStateOf(false) }

    if (showSavedCredential){
        localActivity?.let { myActivity ->
            coroutineScope.launch {
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
        showSavedCredential = false
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
            onGetSavedCredentialClick = { showSavedCredential = true },
            versionText = appVersion,
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
        if (e is NoCredentialException) {
            loginViewModel.setEvent(LoginEvent.SetLoadingState)
            delay(DELAY)
            loginViewModel.setEvent(LoginEvent.SetShowState)
            loginViewModel.setEvent(LoginEvent.SetErrorIntent(msg = LoginUiMessages.NO_SAVED_ACCOUNTS.message))
        } else if (e !is GetCredentialProviderConfigurationException
            && e !is GetCredentialCancellationException
        ) {
            loginViewModel.setEvent(LoginEvent.SetErrorIntent(LoginUiMessages.INTERNAL_ERROR.message))
        }
    }
}

private suspend fun showGoogleSignInPopUp(
    myActivity: Activity,
    loginViewModel: LoginViewModel,
) {
    runCatching {
        loginViewModel.setEvent(LoginEvent.SetLoadingState)
        val authCredential: AuthCredential =
            SignInWithCredential().signInWithGoogle(myActivity)
        loginViewModel.setEvent(LoginEvent.SetShowState)
        loginViewModel.setEvent(
            LoginEvent.GoogleAuthCredentialWasChosen(authCredential)
        )
    }.onFailure { e: Throwable ->
        if (e is GetCredentialProviderConfigurationException)
            loginViewModel.setEvent(
                LoginEvent.SetErrorIntent(
                    msg = LoginUiMessages.ABSENT_OF_GOOGLE_ACCOUNT.message
                )
            )
        if (e !is GetCredentialCancellationException)
            loginViewModel.setEvent(LoginEvent.SetErrorIntent(LoginUiMessages.INTERNAL_ERROR.message))
        loginViewModel.setEvent(LoginEvent.SetShowState)
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

private const val DELAY = 3000L