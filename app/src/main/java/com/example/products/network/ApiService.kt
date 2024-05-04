package com.example.products.network

import com.example.products.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("skip") skip: Int? = null,
        @Query("limit") limit: Int? = 20
    ): ProductResponse
}
