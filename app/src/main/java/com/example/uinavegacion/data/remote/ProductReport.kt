package com.example.uinavegacion.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de datos para cada item del reporte que viene del backend.
 */
data class ProductReport(
    // --- CORRECCIÓN: Se alinea con el campo "name" del DTO del backend ---
    @SerializedName("name")
    val name: String,

    @SerializedName("currentStock")
    val currentStock: Int,

    @SerializedName("minStock")
    val minStock: Int,

    @SerializedName("lastUpdate")
    val lastUpdate: String,

    // Se mantienen los campos nulables para robustez
    @SerializedName("categoria")
    val categoria: String?,

    @SerializedName("ubicacion")
    val ubicacion: String?
)
