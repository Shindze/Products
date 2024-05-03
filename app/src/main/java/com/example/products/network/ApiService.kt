package com.example.products.network

import com.example.products.model.ProductResponse
import retrofit2.http.GET

interface ApiService {

    @GET("products?limit=20")
    suspend fun getProducts(): ProductResponse
}