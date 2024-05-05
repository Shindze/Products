package com.example.products.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun saveProducts(listOfProducts: List<Product>, page: Int) {
        val gson = Gson()
        val json = gson.toJson(listOfProducts)
        sharedPreferences.edit().putString("Products$page", json).apply()
    }

    fun saveSearchProducts(listOfProducts: List<Product>) {
        val gson = Gson()
        val json = gson.toJson(listOfProducts)
        sharedPreferences.edit().putString("SearchProducts", json).apply()

        Log.e("Shared сохранение:", listOfProducts.toString())
    }

    fun getSearchProducts(): List<Product>? {
        val json = sharedPreferences.getString("SearchProducts", null)
        val type = object : TypeToken<List<Product>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun getProducts(page: Int): List<Product>? {
        val json = sharedPreferences.getString("Products$page", null)
        val type = object : TypeToken<List<Product>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    fun clearSearchData() {
        sharedPreferences.edit().remove("SearchProducts").apply()
    }


//        fun getQuestionPosition(currentQuestion: Int): Int {
//            val json = sharedPreferences.getString("questionPosition $currentQuestion", 0.toString())
//            val type = object : TypeToken<Int>() {}.type
//            return Gson().fromJson(json, type)
//        }
}
