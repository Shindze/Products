package com.example.products.viewmodel.uiState

import com.example.products.model.Product

data class ProductsUiState(
    val listProducts: MutableList<Product>? = null,
    val listCategories: List<String>? = null,

    val selectedCategoriesToChipState: Map<String, Boolean>? = emptyMap(),
    val selectedCategory: String = "",

    val appState: AppState = AppState.LOADING
)

enum class AppState {
    LOADING, SUCCESS, ERROR
}

