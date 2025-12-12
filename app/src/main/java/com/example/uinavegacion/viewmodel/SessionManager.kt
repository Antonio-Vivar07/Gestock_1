package com.example.uinavegacion.viewmodel

/**
 * Un objeto singleton para gestionar la información de la sesión del usuario actual.
 * Esto nos permite acceder al usuario desde diferentes partes de la app (como el cliente de red)
 * sin tener que pasar el AuthViewModel por todos lados.
 */
object SessionManager {
    // Esta variable guardará una cadena de texto como "antonio (Administrador)"
    var userInfo: String? = null
}
