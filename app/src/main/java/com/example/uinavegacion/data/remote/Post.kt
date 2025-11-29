package com.example.uinavegacion.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que mantiene los mismos nombres usados en la app (userId, id, title, body),
 * pero se mapea a los campos reales del backend Spring Boot.
 */
data class Post(

    // En el backend = "stockActual"
    @SerializedName("stockActual")
    val userId: Int? = null,

    // En el backend = "id"
    @SerializedName("id")
    val id: String? = null,

    // En el backend = "nombre"
    @SerializedName("nombre")
    val title: String,

    // En el backend = "codigoQr"
    @SerializedName("codigoQr")
    val body: String? = null
)
