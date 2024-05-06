package com.example.products.repository

import android.util.Log
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.network.ApiClient
import com.example.products.network.ApiService
import com.example.products.viewmodel.appstate.AppState
import com.example.products.viewmodel.appstate.AppStateManager
import com.example.products.viewmodel.appstate.ProductManager

class ProductsRepository(private val apiService: ApiService) {
    suspend fun fetchProducts(
        skip: Int = 0,
        sharedPrefManager: SharedPrefManager
    ): List<Product>? {

        val currentPage = ProductManager.currentPage.value.currentPage
        val cachedProducts = sharedPrefManager.getProducts(currentPage)

        if (!cachedProducts.isNullOrEmpty()) {
            return cachedProducts
        }

        return try {
            AppStateManager.setState(AppState.LOADING)

            val response = apiService.getProducts(skip)
            val products = response.products

            sharedPrefManager.saveProducts(products, currentPage)
            products
        } catch (e: Exception) {
            Log.e("Репозиторий:", "Не получилось загрузить: ${e.message}")
            AppStateManager.setState(AppState.ERROR)
            null
        }
    }

    suspend fun searchProducts(
        title: String, sharedPrefManager: SharedPrefManager

    ): List<Product>? {
        return try {
            AppStateManager.setState(AppState.LOADING)

            val response = apiService.searchProducts(title)
            val products = response.products

            sharedPrefManager.saveSearchProducts(products)

            products
        } catch (e: Exception) {
            Log.e("Репо:", "Не получилось выполнить поиск: ${e.message}")
            AppStateManager.setState(AppState.ERROR)
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