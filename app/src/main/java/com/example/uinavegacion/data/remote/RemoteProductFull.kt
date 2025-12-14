package com.example.uinavegacion.data.remote

/**
 * Modelo que coincide con el Product del backend (Mongo).
 * Se usa para listar / borrar / restaurar.
 *
 * Nota: lastUpdate puede venir como timestamp o string según Jackson, por eso se tipa como Any.
 */
data class RemoteProductFull(
    val id: String,
    val nombre: String,
    val codigoQr: String,
    val stockActual: Int? = 0,
    val categoria: String? = "",
    val ubicacion: String? = "",
    val minStock: Int? = 0,
    val lastUpdate: Any? = null,
    val status: String? = "ACTIVE",
    val lastModifiedBy: String? = null
)
