package com.example.uinavegacion.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    ): RemoteLoginResponse

    // Obtener todos los usuarios
    @GET("api/users")
    suspend fun getUsers(): List<RemoteUser>

    // Cambiar rol de usuario (solo ADMIN)
    @PUT("api/users/{username}/role")
    suspend fun updateUserRole(
        @Path("username") username: String,
        @Body body: UpdateRoleRequest
    )
}
