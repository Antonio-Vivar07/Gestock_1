package com.example.uinavegacion.data.remote

<<<<<<< Updated upstream
import com.example.uinavegacion.viewmodel.UserRole

/**
 * Representa la respuesta del backend al hacer login o registro.
 * Incluye el token JWT necesario para la autenticación.
=======
/**
 * Respuesta del backend al hacer login.
 * Coincide con LoginResponse (Spring Boot).
>>>>>>> Stashed changes
 */
data class RemoteUserResponse(
    val username: String,
    val email: String,
    val role: String,
<<<<<<< Updated upstream
    val jwt: String // El token de autenticación
=======
    val jwt: String
>>>>>>> Stashed changes
)
