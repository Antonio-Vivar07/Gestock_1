package com.example.uinavegacion.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface InventoryApiService {

    // Coincide con tu ProductController @RequestMapping("/api/products")
    @POST("api/products")
    suspend fun createProduct(@Body product: RemoteProduct): RemoteProduct
}