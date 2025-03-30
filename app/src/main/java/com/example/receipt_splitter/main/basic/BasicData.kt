package com.example.receipt_splitter.main.basic

interface BasicUiState
interface BasicIntent
interface BasicUiEvent
interface BasicUiErrorIntent

sealed interface BasicFunResponse{
    object onSuccess : BasicFunResponse
    class onError(val msg: String) : BasicFunResponse
}