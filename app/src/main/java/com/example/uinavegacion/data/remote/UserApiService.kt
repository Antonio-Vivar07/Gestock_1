package com.example.uinavegacion.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {
    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RemoteUser>

    @POST("api/users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<RemoteUser>

    // --- ¡MÉTODO AÑADIDO PARA LA SINCRONIZACIÓN! ---
    @GET("api/users")
    suspend fun getAllUsers(): List<RemoteUser>
}
