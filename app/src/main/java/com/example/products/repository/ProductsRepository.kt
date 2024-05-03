package com.example.products.repository

import android.util.Log
import com.example.products.model.Product
import com.example.products.network.ApiClient
import com.example.products.network.ApiService

class ProductsRepository(private val apiService: ApiService) {

    private val productsRatesCache = mutableListOf<Product>()

    suspend fun getProducts(): List<Product> {

        if (productsRatesCache.isNotEmpty()) {
            Log.e("Кэш выдан:", productsRatesCache.toString())
            return productsRatesCache
        }

        val response = apiService.getProducts()
        Log.e("Данные запрошены:", response.toString())

        productsRatesCache.addAll(response.products)

        return productsRatesCache
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductsRepository? = null

        fun getRepository(): ProductsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProductsRepository(ApiClient.apiService).also { INSTANCE = it }
            }
        }
    }
}