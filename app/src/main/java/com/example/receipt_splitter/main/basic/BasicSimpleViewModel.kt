package com.example.receipt_splitter.main.basic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BasicSimpleViewModel<
        basicUiState : BasicUiState,
        basicEvent : BasicEvent,
        basicUiMessageIntent : BasicUiMessageIntent>(initialUiState: basicUiState) : ViewModel() {

    private val uiState = MutableStateFlow(initialUiState)
    private val uiStateFlow = uiState.asStateFlow()

    private val uiMessageIntent = MutableSharedFlow<basicUiMessageIntent?>(
        replay = 1,
        extraBufferCapacity = 3,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val uiMessageIntentFlow = uiMessageIntent.asSharedFlow()

    protected fun setUiState(newUiState: basicUiState) {
        uiState.value = newUiState
    }

    protected fun setUiMessageIntent(newUiErrorIntent: basicUiMessageIntent) {
        uiMessageIntent.tryEmit(newUiErrorIntent)
    }

    fun getUiStateFlow() = uiStateFlow
    fun getUiMessageIntentFlow() = uiMessageIntentFlow

    fun clearUiMessageIntentFlow() {
        uiMessageIntent.tryEmit(null)
    }

    abstract fun setEvent(newEvent: basicEvent)
}