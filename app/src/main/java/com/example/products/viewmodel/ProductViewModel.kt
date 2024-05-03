package com.example.products.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.SharedPrefManager
import com.example.products.viewmodel.uiState.ProductScreenUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel(context: Context) : ViewModel() {

    private val _listOfProducts: MutableStateFlow<ProductScreenUiState> = // Пересмотреть и изменить
        MutableStateFlow(ProductScreenUiState())
    val listOfProducts: StateFlow<ProductScreenUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    init {
        getProducts()
    }

    private fun getProducts() {
        _listOfProducts.value = _listOfProducts.value.copy(
            listProducts = sharedPrefManager.getProducts()
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}