package com.example.products.viewmodel.appstate

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppState {
    LOADING, SUCCESS, ERROR
}


object AppStateManager {

    private val _status = MutableStateFlow(AppState.LOADING)
    val status: StateFlow<AppState> = _status.asStateFlow()

    fun setState(state: AppState) {

        Log.e("Состояние изменено:", state.toString())

        _status.value = state
    }
}

