package com.example.products.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.products.model.SharedPrefManager
import com.example.products.repository.ProductsRepository
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.uiState.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(context: Context) : ViewModel() {

    private val _listOfProducts: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState())
    val listOfProducts: StateFlow<SearchUiState> = _listOfProducts.asStateFlow()

    var textFieldValue by mutableStateOf("")

    private val repo = ProductsRepository.getRepository()

    private val sharedPrefManager = SharedPrefManager(context)

    init {
        sharedPrefManager.clearSearchData()
    }

    fun searchItems() {
        viewModelScope.launch {
            try {

                val products = repo.searchProducts(textFieldValue, sharedPrefManager)
                _listOfProducts.value = _listOfProducts.value.copy(listProducts = products)

                updateAppState(AppState.SUCCESS)
            } catch (e: Exception) {
                Log.e("SearchViewModel:", "Ошибка при выполнении поиска: ${e.message}")
            }
        }
    }

    private fun updateAppState(state: AppState) {
        AppStateManager.setState(state)
    }
}