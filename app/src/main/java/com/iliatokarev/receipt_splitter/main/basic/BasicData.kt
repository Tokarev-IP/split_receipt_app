package com.iliatokarev.receipt_splitter.main.basic

interface BasicUiState
interface BasicIntent
interface BasicEvent
interface BasicNavigationEvent
interface BasicUiMessageIntent

sealed interface BasicFunResponse{
    object Success : BasicFunResponse
    class Error(val msg: String) : BasicFunResponse
}