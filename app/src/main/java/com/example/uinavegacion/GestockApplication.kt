package com.example.uinavegacion

import android.app.Application
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.repository.PostRepository
import com.example.uinavegacion.repository.ProductRepository
import com.example.uinavegacion.repository.UserRepository

interface AppContainer {
    val userRepository: UserRepository
    val productRepository: ProductRepository
    val postRepository: PostRepository
}

class DefaultAppContainer(application: Application) : AppContainer {

    private val db by lazy {
        AppDatabase.getInstance(application)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(db.userDao())
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(
            db.productDao(),
            db.movimientoDao(),
            RetrofitClient.inventoryApiService,
            RetrofitClient.movementApiService
        )
    }

    // Corregido: Se eliminó la declaración duplicada que causaba el error
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