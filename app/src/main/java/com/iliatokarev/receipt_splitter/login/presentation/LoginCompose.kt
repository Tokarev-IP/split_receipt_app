package com.iliatokarev.receipt_splitter.login.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.iliatokarev.receipt_splitter.R
import com.iliatokarev.receipt_splitter.login.data.SignInWithCredential
import com.iliatokarev.receipt_splitter.login.presentation.screens.EmailVerificationScreen
import com.iliatokarev.receipt_splitter.login.presentation.screens.RegistrationScreen
import com.iliatokarev.receipt_splitter.login.presentation.screens.ResetPasswordScreen
import com.iliatokarev.receipt_splitter.login.presentation.screens.SignInLoadingScreen
import com.iliatokarev.receipt_splitter.login.presentation.screens.SignInScreen
import com.iliatokarev.receipt_splitter.main.presentation.MainEvent
import com.iliatokarev.receipt_splitter.main.presentation.MainViewModel

@Composable
fun LoginCompose(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: LoginNavHostDestinations = LoginNavHostDestinations.SignInLoadingScreenNav,
    loginViewModel: LoginViewModel,
    mainViewModel: MainViewModel,
    myActivity: Activity? = LocalActivity.current
) {
    val uiMessageTextList = mapOf<String, String>(
        LoginUiMessages.INTERNAL_ERROR.message to stringResource(R.string.internal_error),
        LoginUiMessages.BLOCKED_ALL_REQUESTS.message to stringResource(R.string.too_many_requests_try_again_later),
        LoginUiMessages.NETWORK_ERROR.message to stringResource(R.string.no_internet_connection),
        LoginUiMessages.EMAIL_IS_NOT_VERIFIED.message to stringResource(R.string.email_is_not_verified),
        LoginUiMessages.RESET_PASSWORD_EMAIL_WAS_SENT.message to stringResource(R.string.password_reset_email_was_sent),
        LoginUiMessages.VERIFICATION_EMAIL_WAS_SENT.message to stringResource(R.string.email_verification_has_been_sent),
        LoginUiMessages.ABSENT_OF_GOOGLE_ACCOUNT.message to stringResource(R.string.absent_of_google_account),
        LoginUiMessages.NO_SAVED_ACCOUNTS.message to stringResource(R.string.no_saved_accounts),
    )

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getIntentFlow().collect { intent ->
            handleLoginIntent(
                intent = intent,
                navHostController = navHostController,
                mainViewModel = mainViewModel,
                loginViewModel = loginViewModel,
                myActivity = myActivity
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow().collect { intent ->
            myActivity?.let { activity ->
                handleUiMessages(intent, activity, uiMessageTextList)
            }
        }
    }

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navHostController,
        startDestination = startDestination,
    ) {
        composable<LoginNavHostDestinations.SignInLoadingScreenNav> {
            SignInLoadingScreen(loginViewModel = loginViewModel)
        }

        composable<LoginNavHostDestinations.ChooseSignInOptionScreenNav> {
            SignInScreen(loginViewModel = loginViewModel)
        }

        composable<LoginNavHostDestinations.RegistrationScreenNav> {
            RegistrationScreen(loginViewModel = loginViewModel)
        }

        composable<LoginNavHostDestinations.EmailVerificationScreenNav> { backStackEntry ->
            val emailVerificationScreenData =
                backStackEntry.toRoute<LoginNavHostDestinations.EmailVerificationScreenNav>()
            EmailVerificationScreen(
                loginViewModel = loginViewModel,
                email = emailVerificationScreenData.email,
                password = emailVerificationScreenData.password,
            )
        }

        composable<LoginNavHostDestinations.ResetPasswordScreenNav> {
            ResetPasswordScreen(loginViewModel = loginViewModel)
        }
    }
}

private suspend fun handleEmailAndPasswordSavingPopUp(
    myActivity: Activity,
    email: String,
    password: String,
    loginViewModel: LoginViewModel,
) {
    runCatching {
        SignInWithCredential().registerPassword(
            username = email,
            password = password,
            activity = myActivity
        )
    }.onFailure { e: Throwable ->
        if (e !is CreateCredentialCancellationException && e !is CreateCredentialUnknownException)
            loginViewModel.setEvent(LoginEvent.SetErrorIntent(e.message.toString()))
    }
}

private suspend fun handleLoginIntent(
    intent: LoginIntent,
    navHostController: NavHostController,
    mainViewModel: MainViewModel,
    loginViewModel: LoginViewModel,
    myActivity: Activity?
) {
    when (intent) {
        is LoginIntent.GoToSignInScreen -> {
            navHostController.navigate(LoginNavHostDestinations.ChooseSignInOptionScreenNav) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        is LoginIntent.UserIsAuthorized -> {
            mainViewModel.setEvent(MainEvent.UserIsSignedIn)
        }

        is LoginIntent.GoToRegistrationScreen -> {
            navHostController.navigate(LoginNavHostDestinations.RegistrationScreenNav)
        }

        is LoginIntent.GoToEmailVerificationScreen -> {
            navHostController.navigate(
                LoginNavHostDestinations.EmailVerificationScreenNav(
                    intent.email,
                    intent.password
                )
            )
        }

        is LoginIntent.GoToResetPasswordScreen -> {
            navHostController.navigate(LoginNavHostDestinations.ResetPasswordScreenNav)
        }

        is LoginIntent.GoNavigationBack -> {
            navHostController.popBackStack()
        }

        is LoginIntent.ShowSaveCredentialsPopup -> {
            myActivity?.let {
                handleEmailAndPasswordSavingPopUp(
                    myActivity = it,
                    email = intent.email,
                    password = intent.password,
                    loginViewModel = loginViewModel,
                )
            }
        }
    }
}

private fun handleUiMessages(
    uiMessageIntent: LoginUiMessageIntent,
    currentActivity: Activity,
    uiMessagesMap: Map<String, String>,
) {
    when (uiMessageIntent) {
        is LoginUiMessageIntent.ErrorMessage -> {
            Toast.makeText(
                currentActivity,
                uiMessageIntent.msg,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.InternalError -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.INTERNAL_ERROR.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.TooManyRequests -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.BLOCKED_ALL_REQUESTS.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.NoInternetConnection -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.NETWORK_ERROR.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.EmailIsNotVerified -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.EMAIL_IS_NOT_VERIFIED.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.ResetPasswordEmailWasSent -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.RESET_PASSWORD_EMAIL_WAS_SENT.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.VerificationEmailWasSent -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.VERIFICATION_EMAIL_WAS_SENT.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.AbsentOfGoogleAccount -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.ABSENT_OF_GOOGLE_ACCOUNT.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.NoSavedAccounts -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap[LoginUiMessages.NO_SAVED_ACCOUNTS.message]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}