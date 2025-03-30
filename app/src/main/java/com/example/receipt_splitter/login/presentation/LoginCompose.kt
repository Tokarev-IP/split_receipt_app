package com.example.receipt_splitter.login.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.receipt_splitter.login.data.SignInWithGoogle
import com.example.receipt_splitter.login.presentation.screens.ChooseSignInOptionScreen
import com.example.receipt_splitter.login.presentation.screens.SignInLoadingScreen
import com.example.receipt_splitter.main.presentation.MainUiEvent
import com.example.receipt_splitter.main.presentation.MainViewModel
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginCompose(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    startDestination: LoginNavHostDestinations = LoginNavHostDestinations.SignInLoadingScreenNav,
    composableScope: CoroutineScope = rememberCoroutineScope(),
    loginViewModel: LoginViewModel,
    mainViewModel: MainViewModel,
) {
    val intent by loginViewModel.getIntentFlow().collectAsState(initial = null)

    when (intent) {
        is LoginIntent.UserHasToSignIn -> {
            loginViewModel.clearIntentFlow()
            navHostController.navigate(LoginNavHostDestinations.ChooseSignInOptionScreenNav)
            loginViewModel.setUiEvent(LoginUiEvent.SetShowState)
        }

        is LoginIntent.ShowGoogleSignInPopUp -> {
            loginViewModel.clearIntentFlow()
            LocalActivity.current?.let { myActivity ->
                composableScope.launch {
                    runCatching {
                        val authCredential: AuthCredential? =
                            SignInWithGoogle().signInWithGoogle(myActivity)
                        loginViewModel.setUiEvent(
                            LoginUiEvent.GoogleAuthCredentialWasChosen(
                                authCredential
                            )
                        )
                    }.onFailure {
                        loginViewModel.setUiEvent(LoginUiEvent.SetShowState)
                    }
                }
            }
        }

        is LoginIntent.UserHasSignedIn -> {
            loginViewModel.clearIntentFlow()
            mainViewModel.setUiEvent(MainUiEvent.UserIsSignedIn((intent as LoginIntent.UserHasSignedIn).userId))
            loginViewModel.setUiEvent(LoginUiEvent.SetShowState)
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
            ChooseSignInOptionScreen(loginViewModel = loginViewModel)
        }
    }
}