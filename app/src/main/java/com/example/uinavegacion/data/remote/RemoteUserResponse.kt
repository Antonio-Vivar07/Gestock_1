package com.example.uinavegacion.data.remote

import com.example.uinavegacion.viewmodel.UserRole

/**
 * Representa la respuesta del backend al hacer login o registro.
 * Incluye el token JWT necesario para la autenticación.
 */
data class RemoteUserResponse(
    val username: String,
    val email: String,
    val role: String,
    val jwt: String // El token de autenticación
)
