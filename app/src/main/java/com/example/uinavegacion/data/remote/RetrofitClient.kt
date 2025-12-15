package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.local.session.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit central.
 * Se inicializa una vez con SessionManager para que el interceptor agregue JWT + X-User-Info.
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null

    fun init(sessionManager: SessionManager) {
        if (retrofit != null) return

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun requireRetrofit(): Retrofit =
        retrofit ?: throw IllegalStateException("RetrofitClient no inicializado. Llama RetrofitClient.init(...) en Application.")

    val inventoryApiService: InventoryApiService by lazy {
        requireRetrofit().create(InventoryApiService::class.java)
    }

    val movementApiService: MovementApiService by lazy {
        requireRetrofit().create(MovementApiService::class.java)
    }

    val postApiService: PostApiService by lazy {
        requireRetrofit().create(PostApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        requireRetrofit().create(UserApiService::class.java)
    }

    val productApiService: ProductApiService by lazy {
        requireRetrofit().create(ProductApiService::class.java)
    }

    val reportApiService: ReportApiService by lazy {
        requireRetrofit().create(ReportApiService::class.java)
    }
}
