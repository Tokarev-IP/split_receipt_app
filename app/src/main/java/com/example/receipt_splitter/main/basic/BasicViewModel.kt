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
        basicEvent : BasicEvent,
        basicUiMessageIntent : BasicUiMessageIntent>(initialUiState: basicUiState) : ViewModel() {

    private val uiState = MutableStateFlow(initialUiState)
    private val uiStateFlow = uiState.asStateFlow()

    private val intent = MutableSharedFlow<basicIntent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    private val intentFlow = intent.asSharedFlow()

    private val uiMessageIntent = MutableSharedFlow<basicUiMessageIntent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    private val uiMessageIntentFlow = uiMessageIntent.asSharedFlow()

    protected fun setUiState(newUiState: basicUiState) {
        uiState.value = newUiState
    }

    protected fun setIntent(newIntent: basicIntent) {
        intent.tryEmit(newIntent)
    }

    protected fun setUiMessageIntent(newUiErrorIntent: basicUiMessageIntent) {
        uiMessageIntent.tryEmit(newUiErrorIntent)
    }

    fun getUiStateFlow() = uiStateFlow
    fun getIntentFlow() = intentFlow
    fun getUiMessageIntentFlow() = uiMessageIntentFlow

    abstract fun setEvent(newEvent: basicEvent)
}

abstract class BasicLoginViewModel<
        basicUiState : BasicUiState,
        basicIntent : BasicIntent,
        basicEvent : BasicEvent,
        basicNavigationEvent : BasicNavigationEvent,
        basicUiMessageIntent : BasicUiMessageIntent>(initialUiState: basicUiState) : ViewModel() {

    private val uiState = MutableStateFlow(initialUiState)
    private val uiStateFlow = uiState.asStateFlow()

    private val intent = MutableSharedFlow<basicIntent>(
        replay = 0,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val intentFlow = intent.asSharedFlow()

    private val uiMessageIntent = MutableSharedFlow<basicUiMessageIntent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    private val uiMessageIntentFlow = uiMessageIntent.asSharedFlow()

    protected fun setUiState(newUiState: basicUiState) {
        uiState.value = newUiState
    }

    protected fun setIntent(newIntent: basicIntent) {
        intent.tryEmit(newIntent)
    }

    protected fun setUiMessageIntent(newUiErrorIntent: basicUiMessageIntent) {
        uiMessageIntent.tryEmit(newUiErrorIntent)
    }

    fun getUiStateFlow() = uiStateFlow
    fun getIntentFlow() = intentFlow
    fun getUiMessageIntentFlow() = uiMessageIntentFlow

    abstract fun setEvent(newEvent: basicEvent)
    abstract fun setNavigationEvent(newNavigationEvent: basicNavigationEvent)
}

abstract class BasicSimpleViewModel<basicEvent : BasicEvent>() : ViewModel() {
    abstract fun setEvent(newEvent: basicEvent)
}