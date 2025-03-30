package com.example.receipt_splitter.main.basic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BasicViewModel<
        basicUiState : BasicUiState,
        basicIntent : BasicIntent,
        basicUiEvent : BasicUiEvent,
        basicUiErrorIntent : BasicUiErrorIntent>(initialUiState: basicUiState) : ViewModel() {

    private val uiState = MutableStateFlow(initialUiState)
    private val uiStateFlow = uiState.asStateFlow()

    private val intent = MutableSharedFlow<basicIntent?>(
        replay = 1,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val intentFlow = intent.asSharedFlow()

    private val uiErrorIntent = MutableSharedFlow<basicUiErrorIntent?>(
        replay = 1,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val uiErrorIntentFlow = uiErrorIntent.asSharedFlow()

    protected fun setUiState(newUiState: basicUiState) {
        uiState.value = newUiState
    }
    protected fun setIntent(newIntent: basicIntent) {
        intent.tryEmit(newIntent)
    }
    protected fun setUiErrorIntent(newUiErrorIntent: basicUiErrorIntent) {
        uiErrorIntent.tryEmit(newUiErrorIntent)
    }

    fun getUiStateFlow() = uiStateFlow
    fun getIntentFlow() = intentFlow
    fun getUiErrorIntentFlow() = uiErrorIntentFlow

    fun clearIntentFlow() {
        intent.tryEmit(null)
    }
    fun clearUiErrorIntentFlow() {
        uiErrorIntent.tryEmit(null)
    }

    abstract fun setUiEvent(newUiEvent: basicUiEvent)
}