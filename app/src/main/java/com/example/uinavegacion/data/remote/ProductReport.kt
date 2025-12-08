package com.example.uinavegacion.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar el estado de un producto en el reporte.
 * Este objeto es el que se recibe desde el backend.
 */
data class ProductReport(
    @SerializedName("name")
    val name: String,

    @SerializedName("currentStock")
    val currentStock: Int,

    @SerializedName("minStock")
    val minStock: Int,

    @SerializedName("lastUpdate")
    val lastUpdate: String
)
