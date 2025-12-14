package com.example.uinavegacion

import android.app.Application
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.local.session.SessionManager
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.repository.PostRepository
import com.example.uinavegacion.repository.ProductRepository
import com.example.uinavegacion.repository.UserRepository

interface AppContainer {
    val sessionManager: SessionManager
    val userRepository: UserRepository
    val productRepository: ProductRepository
    val postRepository: PostRepository
}

class DefaultAppContainer(application: Application) : AppContainer {

    // En este proyecto AppDatabase expone getInstance(...), no getDatabase(...)
    private val database: AppDatabase by lazy { AppDatabase.getInstance(application) }

    override val sessionManager: SessionManager by lazy { SessionManager(application.applicationContext) }

    init {
        // Inicializa Retrofit con interceptor de sesión
        RetrofitClient.init(sessionManager)
    }

    override val userRepository: UserRepository by lazy {
<<<<<<< Updated upstream
        // --- ¡CORRECCIÓN! Se añade el userApiService que faltaba ---
        UserRepository(db.userDao(), RetrofitClient.userApiService)
=======
        UserRepository(
            database.userDao(),
            sessionManager,
            RetrofitClient.userApiService
        )
>>>>>>> Stashed changes
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(
            database.productDao(),
            database.movimientoDao(),
            RetrofitClient.inventoryApiService,
            RetrofitClient.movementApiService,
            RetrofitClient.productApiService,
            RetrofitClient.reportApiService
        )
    }

    override val postRepository: PostRepository by lazy {
        PostRepository(RetrofitClient.postApiService)
    }
}

class GestockApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
