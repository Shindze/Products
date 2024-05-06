package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.SharedPrefManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.ProductsUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel(context: Context) : ViewModel() {

    // Пересмотреть и изменить, а может и нет
    private val _listOfProducts: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState())
    val listOfProducts: StateFlow<ProductsUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    private var isSearch: Boolean = ProductManager.searchNavigate.value.isSearch

    init {
        Log.e("ProductViewModel INIT:", isSearch.toString())
        getProducts(isSearch)
    }

    private fun getProducts(isSearch: Boolean) {
        try {
            val products = when {
                ProductManager.searchNavigate.value.isFiltered -> {
                    sharedPrefManager.getFilteredProducts()
                }
                sharedPrefManager.getSearchProducts().isNullOrEmpty() || !isSearch -> {
                    sharedPrefManager.getProducts(ProductManager.currentPage.value.currentPage)
                }
                else -> {
                    sharedPrefManager.getSearchProducts()
                }
            }

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