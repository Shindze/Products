package com.example.products.viewmodel.appstate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProductManager {

    private val _currentProduct = MutableStateFlow(CurrentProductState())
    val currentProduct: StateFlow<CurrentProductState> = _currentProduct.asStateFlow()

    fun updateCurrentProduct(newProduct: Int) {
        if (_currentProduct.value.currentProduct != newProduct) {
            _currentProduct.value = _currentProduct.value.copy(currentProduct = newProduct)
        }
    }
}

data class CurrentProductState(
    val currentProduct: Int = 1,
)