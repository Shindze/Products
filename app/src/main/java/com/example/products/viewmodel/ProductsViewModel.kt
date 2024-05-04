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
import com.example.products.viewmodel.uiState.MainScreenUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(context: Context) : ViewModel() {

    private val repo = ProductsRepository.getRepository()

    private val _listOfProducts: MutableStateFlow<MainScreenUiState> =
        MutableStateFlow(MainScreenUiState())
    val listOfProducts: StateFlow<MainScreenUiState> = _listOfProducts.asStateFlow()

    private val sharedPrefManager = SharedPrefManager(context)

    private var count: Int = 0

    init {

        Log.e("Вьюмодель инит:", sharedPrefManager.getProducts().toString())

        // Сохранено? Выгружаем
        if (sharedPrefManager.getProducts() != null) {
            _listOfProducts.value = _listOfProducts.value.copy(
                listProducts = sharedPrefManager.getProducts()
            )
            AppStateManager.setState(AppState.SUCCESS)
        } else {

            Log.e("Вьюмодель инит:", "Запрос")
            // Не сохранено? Загружаем
            getProducts()
        }
    }

    private fun getProducts(count: Int = 0) {

        loadData()

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val rates = repo.fetchProducts(count, sharedPrefManager)
                processFetchedProducts(rates)
            } catch (e: Exception) {
                logError("Вьюмодель ошибка запроса: ${e.message}")
                updateAppState(AppState.ERROR)
            }
        }
    }

    private fun processFetchedProducts(rates: List<Product>?) {
        if (rates.isNullOrEmpty()) {
            logError("Нулл или пусто")
            updateAppState(AppState.ERROR)
        } else {
            Log.e("Данные:", rates.toString())
            _listOfProducts.value = _listOfProducts.value.copy(listProducts = rates)
            updateAppState(AppState.SUCCESS)
        }
    }

    private fun logError(message: String) {
        Log.e("Данные не получены:", _listOfProducts.value.toString())
        Log.e("Ошибка:", message)
    }

    private fun updateAppState(state: AppState) {
        AppStateManager.setState(state)
    }


    fun updateProducts() {
        count = 0
        sharedPrefManager.clearKey()
        getProducts()
    }

    fun changePage(direction: Boolean) {
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
        getProducts(count)
    }

    private fun loadData() {
        viewModelScope.launch() {
            AppStateManager.setState(AppState.LOADING)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}