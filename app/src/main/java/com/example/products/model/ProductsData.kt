package com.example.products.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double?,
    val rating: Double?,
    val brand: String?,
    val stock: Int? = null,
    val category: String? = null,
    val thumbnail: String,
    val images: List<String>
)

data class ProductResponse(
    val products: List<Product>,
    val total: Int
)

// Старые категории
//typealias CategoriesResponse = List<String>

// Новые категории
data class CategoriesResponse(
    val slug: String,
    val name: String,
    val url: String
)
