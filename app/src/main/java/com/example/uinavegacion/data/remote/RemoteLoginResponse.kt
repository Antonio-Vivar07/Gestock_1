package com.example.uinavegacion.data.remote

data class RemoteLoginResponse(
    val username: String,
    val email: String,
    val role: String,
    val jwt: String
)
