package com.example.uinavegacion.data.remote

data class RemoteUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val createdAt: String
)
