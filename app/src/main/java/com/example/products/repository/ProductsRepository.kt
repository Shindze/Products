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
    suspend fun fetchCacheProducts(
        skip: Int = 0, sharedPrefManager: SharedPrefManager, category: String
    ): List<Product>? {

        val currentPage = ProductManager.currentPage.value.currentPage
        val cachedProducts = sharedPrefManager.getProducts(currentPage)

        return if (!cachedProducts.isNullOrEmpty() && category.isEmpty()) {
            Log.e("Repository:", "Выгружен кэш прод")
            cachedProducts

        } else fetchProducts(skip, sharedPrefManager, category)
    }

    suspend fun fetchCacheCategories(
        sharedPrefManager: SharedPrefManager
    ): List<String>? {

        val cachedProducts = sharedPrefManager.getCategories()

        return if (!cachedProducts.isNullOrEmpty()) {
            Log.e("Repository:", "Выгружен кэш категории")
            cachedProducts

        } else fetchCategories(sharedPrefManager)
    }

    private suspend fun fetchProducts(
        skip: Int = 0, sharedPrefManager: SharedPrefManager, category: String
    ): List<Product>? {

        val currentPage = ProductManager.currentPage.value.currentPage

        Log.e("Repository:", "Выбранная категория: $category")

        return try {

            updateAppState(state = AppState.LOADING)

            return if (category.isNotEmpty()) {
                val response = apiService.getProducts(skip, limit = 100)
                val products = response.products

                val filteredProducts = filterProductsByCategory(products, category)

                sharedPrefManager.saveFilteredProducts(products)

                return filteredProducts
            } else {
                val response = apiService.getProducts(skip)
                val products = response.products

                sharedPrefManager.saveProducts(products, currentPage)
                products
            }

        } catch (e: Exception) {
            Log.e("Repository:", "Ошибка при загрузке продуктов: ${e.message}")
            AppStateManager.setState(AppState.ERROR)
            null
        }
    }

    private fun filterProductsByCategory(products: List<Product>, category: String): List<Product> {
        return products.filter { product ->
            product.category?.equals(category, ignoreCase = true) ?: false
        }
    }

    private suspend fun fetchCategories(sharedPrefManager: SharedPrefManager): List<String>? {
        return try {
            AppStateManager.setState(AppState.LOADING)
            val categories = apiService.getCategories()

            sharedPrefManager.saveCategories(categories)

            categories
        } catch (e: Exception) {
            Log.e("Repository:", "Ошибка при загрузке категорий: ${e.message}")
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
            Log.e("Repository:", "Ошибка при выполнении поиска: ${e.message}")
            AppStateManager.setState(AppState.ERROR)
            null
        }
    }

    private fun updateAppState(state: AppState) {
        AppStateManager.setState(state)
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