package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.SharedPrefManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.AppState
import com.example.products.viewmodel.uiState.ProductsUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailedProductViewModel(context: Context) : ViewModel() {

    private val _listOfProducts: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState())
    val listOfProducts: StateFlow<ProductsUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    init {
        getProducts()
    }

    private fun getProducts() {
        Log.e("ProductViewModel:", "getProducts зашел")
        try {
            val products = when {
                ProductManager.filteredState.value.isFiltered -> {
                    sharedPrefManager.getFilteredProducts()
                }

                sharedPrefManager.getSearchProducts().isNullOrEmpty() -> {
                    sharedPrefManager.getAllProducts()
                }

                else -> {
                    sharedPrefManager.getSearchProducts()
                }
            }

            Log.e("ProductViewModel:", "getProducts вышел")

            if (products != null) {
                _listOfProducts.value =
                    _listOfProducts.value.copy(listProducts = products.toMutableList())
                updateAppState(AppState.SUCCESS)
            }

        } catch (e: Exception) {
            Log.e("ProductViewModel:", "Ошибка при получении продукта: ${e.message}")
        }
    }

    private fun updateAppState(newState: AppState) {
        _listOfProducts.value = _listOfProducts.value.copy(
            appState = newState
        )
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}