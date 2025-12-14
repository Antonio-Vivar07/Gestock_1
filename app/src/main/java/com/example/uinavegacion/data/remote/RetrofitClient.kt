package com.example.uinavegacion.data.remote

<<<<<<< Updated upstream
import com.example.uinavegacion.viewmodel.AuthInterceptor
=======
import com.example.uinavegacion.data.local.session.SessionManager
>>>>>>> Stashed changes
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

<<<<<<< Updated upstream
=======
/**
 * Cliente Retrofit central.
 * Se inicializa una vez con SessionManager para que el interceptor agregue JWT + X-User-Info.
 */
>>>>>>> Stashed changes
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
}
