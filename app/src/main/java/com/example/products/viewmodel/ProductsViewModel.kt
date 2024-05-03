package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        // Сохранено? Выгружаем
        if (sharedPrefManager.getProducts() != null) {
            _listOfProducts.value = _listOfProducts.value.copy(
                listProducts = sharedPrefManager.getProducts()
            )
            AppStateManager.setState(AppState.SUCCESS)
        } else {
            // Не сохранено? Загружаем
            getProducts()
        }
    }

    private fun getProducts() {

        loadData()

        viewModelScope.launch(Dispatchers.Main) {
            try {

                val rates = repo.getProducts()

                _listOfProducts.value = _listOfProducts.value.copy(
                    listProducts = rates,
                )
                sharedPrefManager.saveProducts(rates)

                AppStateManager.setState(AppState.SUCCESS)

                Log.e("Данные получены:", listOfProducts.value.toString())
                Log.e("Состояние:", AppStateManager.status.value.toString())
            } catch (e: Exception) {
                AppStateManager.setState(AppState.ERROR)
                Log.e("Данные не получены:", listOfProducts.value.toString())
            }
        }
    }

    fun updateProducts() {
        getProducts()
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