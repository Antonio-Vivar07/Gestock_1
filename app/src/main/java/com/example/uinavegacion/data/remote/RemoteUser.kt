package com.example.uinavegacion.data.remote

data class RemoteUser(
    val id: String? = null,
    val username: String,
    val email: String,
    // En respuestas del backend (GET /api/users) puede venir omitido.
    val password: String? = null,
    val role: String,
    val createdAt: String? = null
)
