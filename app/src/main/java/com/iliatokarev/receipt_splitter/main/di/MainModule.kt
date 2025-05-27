package com.iliatokarev.receipt_splitter.main.di

import com.iliatokarev.receipt_splitter.main.presentation.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainModule = module {
    viewModelOf(::MainViewModel)
}
