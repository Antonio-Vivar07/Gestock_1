package com.example.uinavegacion.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un Post de la API JSONPlaceholder.
 */
data class Post(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
