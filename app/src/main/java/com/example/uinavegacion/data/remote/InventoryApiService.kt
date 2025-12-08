package com.example.uinavegacion.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryApiService {

    @POST("api/products")
    suspend fun createProduct(@Body product: RemoteProduct): RemoteProduct

    @GET("api/inventory/report")
    suspend fun getProductsReport(): List<ProductReport>

    // --- MÉTODOS AÑADIDOS PARA GESTIÓN DE ESTADO ---

    /**
     * Obtiene una lista de productos del backend filtrada por su estado.
     * Se usará para pedir la lista de productos "INACTIVE".
     */
    @GET("api/products/status/{status}")
    suspend fun getProductsByStatus(@Path("status") status: String): List<RemoteProduct>

    /**
     * Actualiza el estado de un producto en el backend.
     * Se usará tanto para "inactivar" (borrar) como para "restaurar".
     */
    @PUT("api/products/status/{id}/{status}")
    suspend fun updateProductStatus(
        @Path("id") productId: String,
        @Path("status") status: String
    ): Response<Void>
}
