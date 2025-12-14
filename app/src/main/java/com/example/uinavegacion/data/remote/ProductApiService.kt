package com.example.uinavegacion.data.remote

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductApiService {

    @GET("api/products")
    suspend fun getActiveProducts(): List<RemoteProductFull>

    @GET("api/products/status/{status}")
    suspend fun getProductsByStatus(@Path("status") status: String): List<RemoteProductFull>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): RemoteProductFull

    @PUT("api/products/{id}/restore")
    suspend fun restoreProduct(@Path("id") id: String): RemoteProductFull
}
