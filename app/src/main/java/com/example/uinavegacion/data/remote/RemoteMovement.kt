package com.example.uinavegacion.data.remote

data class RemoteMovement(
    val productCode: String,
    val quantity: Int,
    val type: String,               // "INGRESO" o "EGRESO"
    val referenceDocument: String?, // Guía / Orden
    val reason: String?,            // Motivo
    val createdAt: String? = null   // lo dejamos null, el backend pone "dd-MM-yyyy HH:mm:ss"
)
