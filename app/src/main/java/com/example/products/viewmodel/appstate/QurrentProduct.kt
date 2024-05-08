package com.example.products.viewmodel.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductManager {

    private val _currentPage = MutableStateFlow(CurrentPageState())
    val currentPage: StateFlow<CurrentPageState> = _currentPage.asStateFlow()

    private val _searchNavigate = MutableStateFlow(FilteredState())
    val searchNavigate: StateFlow<FilteredState> = _searchNavigate.asStateFlow()

    fun updateCurrentPage(newPage: Int) {
        if (_currentPage.value.currentPage != newPage) {
            _currentPage.value = _currentPage.value.copy(currentPage = newPage)
        }
    }

    fun updateFilteredState(_isFiltered: Boolean) {
        if (_searchNavigate.value.isFiltered != _isFiltered) {
            _searchNavigate.value = _searchNavigate.value.copy(isFiltered = _isFiltered)
        }
    }
}

data class CurrentPageState(
    val currentPage: Int = 1,
)

data class FilteredState(
    val isFiltered: Boolean = false,
)