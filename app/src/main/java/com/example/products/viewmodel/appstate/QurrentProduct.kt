package com.example.products.viewmodel.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductManager {

    private val _currentProduct = MutableStateFlow(CurrentProductState())
    val currentProduct: StateFlow<CurrentProductState> = _currentProduct.asStateFlow()

    private val _currentPage = MutableStateFlow(CurrentPageState())
    val currentPage: StateFlow<CurrentPageState> = _currentPage.asStateFlow()

    fun updateCurrentProduct(newProduct: Int) {
        if (_currentProduct.value.currentProduct != newProduct) {
            _currentProduct.value = _currentProduct.value.copy(currentProduct = newProduct)
        }
    }

    fun updateCurrentPage(newPage: Int) {
        if (_currentPage.value.currentPage != newPage) {
            _currentPage.value = _currentPage.value.copy(currentPage = newPage)
        }
    }
}

data class CurrentProductState(
    val currentProduct: Int = 1,
)

data class CurrentPageState(
    val currentPage: Int = 1,
)