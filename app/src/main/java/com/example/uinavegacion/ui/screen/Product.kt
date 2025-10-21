package com.example.uinavegacion.ui.screen

// Modelo de datos centralizado para un Producto
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val stock: Int,
    val category: String, // <-- CAMPO AÑADIDO
    val zone: String      // <-- CAMPO AÑADIDO
)
