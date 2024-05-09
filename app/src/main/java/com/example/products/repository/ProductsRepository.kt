package com.example.products.repository

import android.util.Log
import coil.network.HttpException
import com.example.products.model.Product
import com.example.products.model.SharedPrefManager
import com.example.products.network.ApiClient
import com.example.products.network.ApiService

class ProductsRepository(private val apiService: ApiService) {

    private val cachedAllProductsList: MutableList<Product> = mutableListOf()
    private var total: Int = 0

    suspend fun fetchCacheProducts(
        skip: Int = 0, sharedPrefManager: SharedPrefManager, category: String
    ): List<Product>? {

        val cachedProducts = sharedPrefManager.getAllProducts()

        if (cachedProducts != null) {
            Log.e("fetchCacheProducts:", "Кэш: ${cachedProducts.size} Пропускаем: ${skip}")
        }

        return if (!cachedProducts.isNullOrEmpty() && category.isEmpty()) {

            Log.e("Первая проверка:", "Кэш не пуст, но")

            if (cachedProducts.size > skip) {

                if (cachedAllProductsList.size >= cachedProducts.size) {
                    Log.e("Вторая проверка:", "Кэш ушел")

                    return cachedAllProductsList
                }

                Log.e("Вторая проверка:", "Кэш: ${cachedProducts.size} Пропускаем: ${skip}")

                cachedAllProductsList.addAll(cachedProducts.drop(skip).take(20))

                Log.e(
                    "Вторая проверка:",
                    "Выгрузили кэш. Итоговый список: ${cachedAllProductsList.size}"
                )

                cachedAllProductsList
            } else {
                Log.e("Вторая проверка провал:", "На следующую страницу кэша нет")
                fetchProducts(skip, sharedPrefManager, category)
            }

        } else {
            Log.e("Первая проверка провал:", "Кэш пуст или выбрана категория")
            fetchProducts(skip, sharedPrefManager, category)
        }
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

        Log.e("Repository:", "Инициализация загрузки")
        Log.e("Repository:", "Выбранная категория: $category")

        return try {

            return if (category.isNotEmpty()) {
                val response = apiService.getProducts(skip, total)
                val products = response.products

                val filteredProducts = filterProductsByCategory(products, category)
                Log.e("Repository", "Отфильтрованные категории: $filteredProducts")

                sharedPrefManager.saveFilteredProducts(products)

                return filteredProducts
            } else {
                val response = apiService.getProducts(skip)
                val products = response.products

                total = response.total

                cachedAllProductsList.addAll(products)

                sharedPrefManager.saveAllProducts(cachedAllProductsList)
                cachedAllProductsList
            }

        } catch (e: HttpException) {
            Log.e("Repository:", "Ошибка при загрузке продуктов: ${e.message}")
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
            val categories = apiService.getCategories()

            sharedPrefManager.saveCategories(categories)

            categories
        } catch (e: HttpException) {
            Log.e("Repository:", "Ошибка при загрузке категорий: ${e.message}")
            null
        }
    }

    suspend fun searchProducts(
        title: String, sharedPrefManager: SharedPrefManager

    ): List<Product>? {
        return try {

            val response = apiService.searchProducts(title)
            val products = response.products

            sharedPrefManager.saveSearchProducts(products)

            products
        } catch (e: HttpException) {
            Log.e("Repository:", "Ошибка при выполнении поиска: ${e.message}")
            null
        }
    }

    fun clearAppCache() {
        cachedAllProductsList.clear()
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