package com.example.receipt_splitter.login.di

import com.example.receipt_splitter.login.data.FirebaseAuthentication
import com.example.receipt_splitter.login.data.FirebaseAuthenticationInterface
import com.example.receipt_splitter.login.domain.CurrentUserUseCase
import com.example.receipt_splitter.login.domain.CurrentUserUseCaseInterface
import com.example.receipt_splitter.login.domain.MessageHandlerUseCase
import com.example.receipt_splitter.login.domain.MessageHandlerUseCaseInterface
import com.example.receipt_splitter.login.domain.SignInUseCase
import com.example.receipt_splitter.login.domain.SignInUseCaseInterface
import com.example.receipt_splitter.login.presentation.LoginViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val signInModule = module {
    viewModelOf(::LoginViewModel)
    factoryOf(::SignInUseCase) { bind<SignInUseCaseInterface>() }
    factoryOf(::FirebaseAuthentication) { bind<FirebaseAuthenticationInterface>() }
    factoryOf(::CurrentUserUseCase) { bind<CurrentUserUseCaseInterface>() }
    factoryOf(::MessageHandlerUseCase) { bind<MessageHandlerUseCaseInterface>()}
}