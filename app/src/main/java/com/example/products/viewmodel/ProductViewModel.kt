package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.ProductsUiState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel(context: Context) : ViewModel() {

    private val _listOfProducts: MutableStateFlow<ProductsUiState> = // Пересмотреть и изменить
        MutableStateFlow(ProductsUiState())
    val listOfProducts: StateFlow<ProductsUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    private var isSearch: Boolean = ProductManager.searchNavigate.value.isSearch

    init {

        getProducts(isSearch)
    }

    private fun getProducts(isSearch: Boolean) {
        try {
            if (sharedPrefManager.getSearchProducts().isNullOrEmpty() || !isSearch) {
                _listOfProducts.value = _listOfProducts.value.copy(
                    listProducts = sharedPrefManager.getProducts(ProductManager.currentPage.value.currentPage)
                )
            } else if (isSearch) {
                _listOfProducts.value = _listOfProducts.value.copy(
                    listProducts = sharedPrefManager.getSearchProducts()
                )
            }
        } catch (e: Exception) {
            Log.e("Ошибка поиска", "Ошибка при получении продуктов: ${e.message}")
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}