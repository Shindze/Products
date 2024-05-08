package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.SharedPrefManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.ProductUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailedProductViewModel(context: Context) : ViewModel() {

    private val _listOfProducts: MutableStateFlow<ProductUiState> =
        MutableStateFlow(ProductUiState())
    val listOfProducts: StateFlow<ProductUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    init {
        getProducts()
    }

    private fun getProducts() {
        Log.e("ProductViewModel:", "getProducts зашел")
        try {
            val products = when {
                ProductManager.searchNavigate.value.isFiltered -> {
                    sharedPrefManager.getFilteredProducts()
                }

                sharedPrefManager.getSearchProducts().isNullOrEmpty() -> {
                    sharedPrefManager.getProducts(ProductManager.currentPage.value.currentPage)
                }

                else -> {
                    sharedPrefManager.getSearchProducts()
                }
            }

            Log.e("ProductViewModel:", "getProducts вышел")

            _listOfProducts.value = _listOfProducts.value.copy(listProducts = products)

        } catch (e: Exception) {
            Log.e("ProductViewModel:", "Ошибка при получении продукта: ${e.message}")
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}