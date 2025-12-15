package com.example.uinavegacion.data.remote

/**
 * Respuesta del backend al hacer login.
 * Coincide con LoginResponse (Spring Boot).
 */
data class RemoteUserResponse(
    val username: String,
    val email: String,
    val role: String,
    val jwt: String
)
