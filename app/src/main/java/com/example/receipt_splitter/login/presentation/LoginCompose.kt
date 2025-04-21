package com.example.receipt_splitter.login.presentation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.receipt_splitter.R
import com.example.receipt_splitter.login.data.SignInWithCredential
import com.example.receipt_splitter.login.presentation.screens.EmailVerificationScreen
import com.example.receipt_splitter.login.presentation.screens.RegistrationScreen
import com.example.receipt_splitter.login.presentation.screens.ResetPasswordScreen
import com.example.receipt_splitter.login.presentation.screens.SignInLoadingScreen
import com.example.receipt_splitter.login.presentation.screens.SignInScreen
import com.example.receipt_splitter.main.presentation.MainEvent
import com.example.receipt_splitter.main.presentation.MainViewModel

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
        "internal_error" to stringResource(R.string.internal_error),
        "too_many_requests_try_again_later" to stringResource(R.string.too_many_requests_try_again_later),
        "no_internet_connection" to stringResource(R.string.no_internet_connection),
        "email_is_not_verified" to stringResource(R.string.email_is_not_verified),
        "password_reset_email_was_sent" to stringResource(R.string.password_reset_email_was_sent),
        "email_verification_has_been_sent" to stringResource(R.string.email_verification_has_been_sent),
    )

    LaunchedEffect(key1 = Unit) {
        loginViewModel.setUiEvent(LoginEvent.SignOut)
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getIntentFlow().collect { intent ->
            intent?.let {
                loginViewModel.clearIntentFlow()
                when (intent) {
                    is LoginIntent.GoToSignInScreen -> {
                        navHostController.navigate(LoginNavHostDestinations.ChooseSignInOptionScreenNav)
                    }

                    is LoginIntent.UserIsAuthorized -> {
                        mainViewModel.setUiEvent(MainEvent.UserIsSignedIn)
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
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        loginViewModel.getUiMessageIntentFlow().collect { intent ->
            intent?.let {
                loginViewModel.clearUiMessageIntentFlow()
                myActivity?.let { activity ->
                    handleUiMessages(intent, activity, uiMessageTextList)
                }
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
) {
    runCatching {
        SignInWithCredential().registerPassword(
            username = email,
            password = password,
            activity = myActivity
        )
    }.onFailure { e: Throwable -> }
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
                uiMessagesMap["internal_error"] ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.TooManyRequests -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap["too_many_requests_try_again_later"]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.NoInternetConnection -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap["no_internet_connection"] ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.EmailIsNotVerified -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap["email_is_not_verified"] ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.ResetPasswordEmailWasSent -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap["password_reset_email_was_sent"]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }

        is LoginUiMessageIntent.VerificationEmailWasSent -> {
            Toast.makeText(
                currentActivity,
                uiMessagesMap["email_verification_has_been_sent"]
                    ?: LoginUiMessages.INTERNAL_ERROR.message,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}