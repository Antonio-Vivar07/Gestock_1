package com.example.uinavegacion.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    @POST("api/users/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RemoteUser>

    // --- CORREGIDO: La respuesta del login ahora incluye el token JWT ---
    @POST("api/users/login")
<<<<<<< Updated upstream
    suspend fun loginUser(@Body request: LoginRequest): Response<RemoteUserResponse>
=======
    suspend fun loginUser(
        @Body loginData: RemoteUserLogin
    ): RemoteLoginResponse
>>>>>>> Stashed changes

    @GET("api/users")
    suspend fun getAllUsers(): List<RemoteUser>

    @PUT("api/users/{username}/role")
    suspend fun updateUserRole(
        @Path("username") username: String,
        @Body request: UpdateRoleRequest
    ): Response<RemoteUser>
}
