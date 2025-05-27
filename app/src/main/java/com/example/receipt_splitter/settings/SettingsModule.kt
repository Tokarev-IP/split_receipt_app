package com.example.receipt_splitter.settings

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)

    factoryOf(::SettingsUseCase) { bind<SettingsUseCaseInterface>() }
}