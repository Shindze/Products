package com.example.products.viewmodel.uiState

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.products.model.Product

data class ProductsUiState(
    val listProducts: List<Product>? = null,
    val listCategories: List<String>? = null,

    val selectedCategoriesToChipState: Map<String, Boolean>? = emptyMap(),
    val selectedCategory: String = "",
)
