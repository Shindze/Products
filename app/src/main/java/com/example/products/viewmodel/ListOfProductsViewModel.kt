package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.repository.ProductsRepository
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.ProductsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListOfProductsViewModel(context: Context) : ViewModel() {

    private val repo = ProductsRepository.getRepository()

    private val _listOfProducts: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState())
    val listOfProducts: StateFlow<ProductsUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)
    private var count: Int = 0

    init {
        Log.e("ProductViewModel INIT", "INIT")
        getProducts()
        getCategories()
    }

    private fun getProducts(numberOfPage: Int = 0, category: String = "") {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val productsResponse =
                    repo.fetchCacheProducts(numberOfPage, sharedPrefManager, category)
                processingFetchedProducts(productsResponse)
            } catch (e: Exception) {
                logError("getProducts ошибка запроса: ${e.message}")
                updateAppState(AppState.ERROR)
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val categoriesResponse = repo.fetchCacheCategories(sharedPrefManager)
                processingFetchedCategories(categoriesResponse)
            } catch (e: Exception) {
                logError("getCategories: Ошибка запроса:${e.message}")
                updateAppState(AppState.ERROR)
            }
        }
    }

    private fun filterProducts() {
        getProducts(category = _listOfProducts.value.selectedCategory)
    }

    //Добавить в начало списка
    fun setFilterChipState(isPressed: Boolean, category: String) {

        val updatedSelectedCategories =
            _listOfProducts.value.selectedCategoriesToChipState?.toMutableMap() ?: mutableMapOf()

        val listCategories = _listOfProducts.value.listCategories?.toMutableList()

        updatedSelectedCategories.clear()
        updatedSelectedCategories[category] = isPressed

        listCategories?.remove(category)
        listCategories?.add(0, category)


        _listOfProducts.value = _listOfProducts.value.copy(
            selectedCategoriesToChipState = updatedSelectedCategories,
            selectedCategory = if (isPressed) category else "",
        )
        ProductManager.updateFilteredState(isPressed)
        if (isPressed) {
            filterProducts()
        } else {
            getProducts()
        }
    }

    private fun processingFetchedProducts(productsResponse: List<Product>?) {
        if (productsResponse.isNullOrEmpty()) {
            logError("Продукты: нулл или пусто")
        } else {
            Log.e("ProductsViewModel:", "Загруженные продукты: $productsResponse")
            _listOfProducts.value = _listOfProducts.value.copy(listProducts = productsResponse)
            updateAppState(AppState.SUCCESS)
        }
    }

    private fun processingFetchedCategories(categoriesResponse: List<String>?) {
        if (categoriesResponse.isNullOrEmpty()) {
            logError("Категории: нулл или пусто")
        } else {
            Log.e("ProductsViewModel:", "Загруженные категории: $categoriesResponse")
            _listOfProducts.value = _listOfProducts.value.copy(listCategories = categoriesResponse)
            updateAppState(AppState.SUCCESS)
        }
    }

    private fun logError(message: String) {
        Log.e(
            "ProductsViewModel:",
            "Продукты не получены: ${_listOfProducts.value.listProducts.toString()}"
        )
        Log.e(
            "ProductsViewModel:",
            "Категории не получены: ${_listOfProducts.value.listCategories.toString()}"
        )
        Log.e("ProductsViewModel", "Ошибка: $message")
    }

    fun updateAllProductsData() {
        count = 0
        ProductManager.updateCurrentPage(newPage = 1)
        sharedPrefManager.clearData()

        _listOfProducts.value = _listOfProducts.value.copy(
            selectedCategoriesToChipState = emptyMap()
        )
        getProducts()
        getCategories()
    }

    fun changePage(direction: Boolean) {
        if (!ProductManager.searchNavigate.value.isFiltered) {
            if (direction) {
                count += when (count <= 60) {
                    true -> 20
                    false -> return
                }
            } else {
                count -= when (count >= 20) {
                    true -> 20
                    false -> return
                }
            }
            getProducts(count, category = _listOfProducts.value.selectedCategory)
        } else return
    }

    private fun updateAppState(state: AppState) {
        AppStateManager.setState(state)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}