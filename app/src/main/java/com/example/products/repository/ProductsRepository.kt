package com.example.products.repository

import android.util.Log
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.network.ApiClient
import com.example.products.network.ApiService

class ProductsRepository(private val apiService: ApiService) {

    private val productsRatesCache = mutableListOf<Product>()

    suspend fun fetchProducts(
        skip: Int = 0, sharedPrefManager: SharedPrefManager
    ): List<Product>? {
        return try {
            //            // Попытка получения кеша
            //            val productsRatesCache = sharedPrefManager.getProducts() ?: listOf()
            //            Log.e("Кэш:", productsRatesCache.toString())

            val response = apiService.getProducts(skip)

            sharedPrefManager.saveProducts(response.products)
            sharedPrefManager.getProducts()

        } catch (e: Exception) {
            Log.e("Репо ошибка", "Ошибка при загрузке продуктов: ${e.message}")
            null
        }
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