package com.example.products.viewmodel.uiState

import com.example.products.model.Product

data class ProductsUiState(
    val listProducts: List<Product>? = null,
    val listCategories: List<String>? = null,

    val selectedCategoriesToChipState: Map<String, Boolean>? = emptyMap(),
    val selectedCategory: String = "",
)

data class SearchUiState(
    val listProducts: List<Product>? = null,
    val listCategories: List<String>? = null,

    val selectedCategoriesToChipState: Map<String, Boolean>? = emptyMap(),
    val selectedCategory: String = "",
)

data class ProductUiState(
    val listProducts: List<Product>? = null,
    val listCategories: List<String>? = null,

    val selectedCategoriesToChipState: Map<String, Boolean>? = emptyMap(),
    val selectedCategory: String = "",
)

