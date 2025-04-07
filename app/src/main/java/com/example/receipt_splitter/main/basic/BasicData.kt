package com.example.receipt_splitter.main.basic

interface BasicUiState
interface BasicIntent
interface BasicUiEvent
interface BasicUiMessageIntent

sealed interface BasicFunResponse{
    object Success : BasicFunResponse
    class Error(val msg: String) : BasicFunResponse
}