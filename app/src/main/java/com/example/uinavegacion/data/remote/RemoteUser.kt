package com.example.uinavegacion.data.remote

data class RemoteUser(
    val id: String? = null,
    val username: String,
    val email: String,
    val password: String,
    val role: String,
    val createdAt: String? = null
)
