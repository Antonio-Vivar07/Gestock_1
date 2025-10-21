package com.example.uinavegacion.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// --- Modelo de Sesión ---
enum class UserRole {
    TRABAJADOR,
    ADMINISTRADOR
}
data class UserSession(val username: String, val role: UserRole)

// --- ViewModel de Autenticación ---
class AuthViewModel : ViewModel() {

    var session: UserSession? by mutableStateOf(null)
        private set

    /**
     * Simula un inicio de sesión y establece la sesión del usuario.
     */
    fun login(username: String) {
        // En una app real, aquí se validarían las credenciales y se guardaría en SharedPreferences.
        val role = if (username.contains("admin", ignoreCase = true)) UserRole.ADMINISTRADOR else UserRole.TRABAJADOR
        session = UserSession(username, role)
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun logout() {
        session = null
    }
}