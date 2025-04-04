package com.example.receipt_splitter.login.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.receipt_splitter.login.presentation.LoginUiEvent
import com.example.receipt_splitter.login.presentation.LoginViewModel
import com.example.receipt_splitter.main.basic.BasicCircularLoadingUi

@Composable
fun SignInLoadingScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
) {
    LaunchedEffect(Unit) {
        loginViewModel.setUiEvent(LoginUiEvent.CheckIfUserIsSignedIn)
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BasicCircularLoadingUi(modifier = modifier.padding(innerPadding))
    }
}