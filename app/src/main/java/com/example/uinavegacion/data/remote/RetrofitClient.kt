package com.example.uinavegacion.data.remote

import com.example.uinavegacion.viewmodel.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // --- ¡AQUÍ ESTÁ LA LÓGICA DE AUDITORÍA! ---
    /**
     * Se crea un cliente de OkHttp personalizado.
     */
    private val okHttpClient: OkHttpClient by lazy {
        // 1. Se crea un interceptor.
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val userInfo = SessionManager.userInfo

            // 2. Si hay un usuario en la sesión, se añade el encabezado.
            val requestWithHeader = if (userInfo != null) {
                originalRequest.newBuilder()
                    .header("X-User-Info", userInfo)
                    .build()
            } else {
                originalRequest
            }
            // 3. La petición continúa su camino.
            chain.proceed(requestWithHeader)
        }

        // 4. Se construye el cliente OkHttp incluyendo el interceptor.
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // --- Se le dice a Retrofit que use nuestro cliente personalizado ---
            .client(okHttpClient)
            .build()
    }

    // --- SERVICIOS (Sin cambios) ---
    val inventoryApiService: InventoryApiService by lazy {
        retrofit.create(InventoryApiService::class.java)
    }

    val movementApiService: MovementApiService by lazy {
        retrofit.create(MovementApiService::class.java)
    }

    val postApiService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}
