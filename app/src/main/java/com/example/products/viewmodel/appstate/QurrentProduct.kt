package com.example.products.viewmodel.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductManager {

    private val _currentPage = MutableStateFlow(CurrentPageState())
    val currentPage: StateFlow<CurrentPageState> = _currentPage.asStateFlow()

    private val _filteredState = MutableStateFlow(FilteredState())
    val filteredState: StateFlow<FilteredState> = _filteredState.asStateFlow()

    fun updateCurrentPage(newPage: Int) {
        if (_currentPage.value.currentPage != newPage) {
            _currentPage.value = _currentPage.value.copy(currentPage = newPage)
        }
    }

    fun updateFilteredState(_isFiltered: Boolean) {
        if (_filteredState.value.isFiltered != _isFiltered) {
            _filteredState.value = _filteredState.value.copy(isFiltered = _isFiltered)
        }
    }

    fun updateSearchState(_isSearched: Boolean) {
        if (_filteredState.value.isSearched != _isSearched) {
            _filteredState.value = _filteredState.value.copy(isSearched = _isSearched)
        }
    }
}

data class CurrentPageState(
    val currentPage: Int = 0,
)

data class FilteredState(
    val isFiltered: Boolean = false,
    val isSearched: Boolean = false,
)