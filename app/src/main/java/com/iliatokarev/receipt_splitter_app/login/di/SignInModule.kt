package com.iliatokarev.receipt_splitter_app.login.di

import com.iliatokarev.receipt_splitter_app.login.data.FirebaseAuthentication
import com.iliatokarev.receipt_splitter_app.login.data.FirebaseAuthenticationInterface
import com.iliatokarev.receipt_splitter_app.login.domain.CurrentUserUseCase
import com.iliatokarev.receipt_splitter_app.login.domain.CurrentUserUseCaseInterface
import com.iliatokarev.receipt_splitter_app.login.domain.MessageHandlerUseCase
import com.iliatokarev.receipt_splitter_app.login.domain.MessageHandlerUseCaseInterface
import com.iliatokarev.receipt_splitter_app.login.domain.SignInUseCase
import com.iliatokarev.receipt_splitter_app.login.domain.SignInUseCaseInterface
import com.iliatokarev.receipt_splitter_app.login.presentation.LoginViewModel
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