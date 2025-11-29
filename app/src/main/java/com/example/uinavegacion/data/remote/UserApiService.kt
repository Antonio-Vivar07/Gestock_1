package com.example.uinavegacion.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApiService {

    // Registrar usuario en el backend
    @POST("api/users/register")
    suspend fun registerUser(
        @Body user: RemoteUser
    )

    // Login de usuario en el backend
    @POST("api/users/login")
    suspend fun loginUser(
        @Body loginData: RemoteUserLogin
    ): RemoteUserResponse

    // Obtener todos los usuarios
    @GET("api/users")
    suspend fun getUsers(): List<RemoteUser>
}
