package com.example.uinavegacion.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Representa la estructura de datos para cada item del reporte que viene del backend.
 * Incluye campos de diagnóstico.
 */
data class ProductReport(
    @SerializedName("name")
    val name: String,

    @SerializedName("currentStock")
    val currentStock: Int,

    @SerializedName("minStock")
    val minStock: Int,

    @SerializedName("lastUpdate")
    val lastUpdate: String,

    @SerializedName("categoria")
    val categoria: String?,

    @SerializedName("ubicacion")
    val ubicacion: String?,

    @SerializedName("lastModifiedBy")
    val lastModifiedBy: String?,

    // --- NUEVOS CAMPOS DE DIAGNÓSTICO ---
    @SerializedName("status")
    val status: String?,

    @SerializedName("codigoQr")
    val codigoQr: String?
)
