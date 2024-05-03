package com.example.products.viewmodel.uiState

import com.example.products.model.Product

data class MainScreenUiState(
    val listProducts: List<Product>? = null
)

data class ProductScreenUiState(
    val listProducts: List<Product>? = null
)