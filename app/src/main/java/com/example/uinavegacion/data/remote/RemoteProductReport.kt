package com.example.uinavegacion.data.remote

data class RemoteProductReport(
    val name: String,
    val currentStock: Int,
    val minStock: Int,
    val lastUpdate: String,
    val categoria: String?,
    val ubicacion: String?
)
