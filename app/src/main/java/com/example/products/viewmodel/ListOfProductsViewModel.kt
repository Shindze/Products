package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.repository.ProductsRepository
import com.example.products.viewmodel.appstate.ProductManager
import com.example.products.viewmodel.uiState.AppState
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

    var textFieldValue by mutableStateOf("")

    init {
        Log.e("ProductViewModel INIT", "INIT")
        getProducts()
        getCategories()
    }

    private fun getProducts(numberOfPage: Int = 0, category: String = "") {

        updateAppState(AppState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
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

        updateAppState(AppState.LOADING)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categoriesResponse = repo.fetchCacheCategories(sharedPrefManager)
                val namesList = categoriesResponse?.map { it.name }

                processingFetchedCategories(namesList)
            } catch (e: Exception) {
                logError("getCategories: Ошибка запроса:${e.message}")
            }
        }
    }

    private fun filterProducts() {
        getProducts(category = _listOfProducts.value.selectedCategory)
    }

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
            updateAppState(AppState.ERROR)

        } else {
            Log.e("ProductsViewModel:", "Загруженные продукты: ${productsResponse.size}")
            _listOfProducts.value = _listOfProducts.value.copy(
                listProducts = productsResponse.toMutableList()
            )
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

    fun searchItems() {

        updateAppState(AppState.LOADING)

        viewModelScope.launch {
            try {
                val products = repo.searchProducts(textFieldValue, sharedPrefManager)
                if (products != null) {
                    _listOfProducts.value =
                        _listOfProducts.value.copy(listSearchProducts = products.toMutableList())
                }

                updateAppState(AppState.SUCCESS)
            } catch (e: Exception) {
                Log.e("SearchViewModel:", "Ошибка при выполнении поиска: ${e.message}")
            }
        }
    }

    private fun logError(message: String) {
        Log.e(
            "ProductsViewModel:",
            "Продукты не получены"
        )
        Log.e(
            "ProductsViewModel:",
            "Категории не получены"
        )
        Log.e("ProductsViewModel", "Ошибка: $message")
    }

    fun updateAllProductsData() {

        count = 0

        ProductManager.updateCurrentPage(newPage = 0)
        ProductManager.updateFilteredState(false)

        repo.clearAppCache()
        sharedPrefManager.clearData()

        _listOfProducts.value = _listOfProducts.value.copy(
            selectedCategoriesToChipState = emptyMap(),
            selectedCategory = ""
        )

        getProducts()
        getCategories()
    }

    fun changePage(direction: Boolean) {
        if (!ProductManager.filteredState.value.isFiltered) {
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
            Log.e("ProductsViewModel", "Грузим")

            getProducts(count, category = _listOfProducts.value.selectedCategory)
        } else return
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