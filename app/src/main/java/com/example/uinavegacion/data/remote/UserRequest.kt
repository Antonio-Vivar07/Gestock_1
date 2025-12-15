package com.example.uinavegacion.data.remote

/**
 * Data class para la petición de registro de usuario.
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String, // CORREGIDO: de 'pass' a 'password'
    val role: String
)

/**
 * Data class para la petición de login.
 */
data class LoginRequest(
    val username: String,
    val password: String // CORREGIDO: de 'pass' a 'password'
)
