package com.example.uinavegacion.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * # Integración de Retrofit para Comunicación con la API
 *
 * (Aquí va la documentación que añadimos antes)
 *
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- ¡CORREGIDO! SE AÑADIERON LOS SERVICIOS QUE FALTABAN ---

    // Servicio de productos (Inventory)
    val inventoryApiService: InventoryApiService by lazy {
        retrofit.create(InventoryApiService::class.java)
    }

    // Servicio de movimientos
    val movementApiService: MovementApiService by lazy {
        retrofit.create(MovementApiService::class.java)
    }

    // Servicio de posts (ya existía)
    val postApiService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }

    // Servicio de usuarios (ya existía)
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}