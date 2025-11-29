package com.example.uinavegacion.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Servicio de productos
    val inventoryApiService: InventoryApiService by lazy {
        retrofit.create(InventoryApiService::class.java)
    }

    // Servicio de movimientos
    val movementApiService: MovementApiService by lazy {
        retrofit.create(MovementApiService::class.java)
    }

    // Servicio de posts
    val postApiService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }

    // Servicio de usuarios
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}
