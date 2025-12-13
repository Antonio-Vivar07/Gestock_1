package com.example.uinavegacion.data.remote

import com.example.uinavegacion.viewmodel.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // --- ¡CORRECCIÓN! Se añade el interceptor de autenticación ---
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val inventoryApiService: InventoryApiService = retrofit.create(InventoryApiService::class.java)
    val postApiService: PostApiService = retrofit.create(PostApiService::class.java)
    val movementApiService: MovementApiService = retrofit.create(MovementApiService::class.java)
    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
}
