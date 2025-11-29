package com.example.uinavegacion.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit centralizado para consumir la API JSONPlaceholder.
 */
object RetrofitClient {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val postApiService: PostApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
    }
}
