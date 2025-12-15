package com.example.uinavegacion.data.remote

data class RemoteProduct(
    val id: String? = null,
    val nombre: String,
    val codigoQr: String,
    val stockActual: Int,
    val categoria: String,
    val ubicacion: String,
    val minStock: Int
)
