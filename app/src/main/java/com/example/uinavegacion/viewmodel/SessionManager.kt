package com.example.uinavegacion.viewmodel

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Objeto singleton para gestionar la información de la sesión del usuario en toda la app.
 */
object SessionManager {
    // Contiene información del usuario para la auditoría (ej: "poblete (Administrador)")
    var userInfo: String? = null

    // Almacena el token JWT recibido después del login para autenticar peticiones
    var jwtToken: String? = null
}

/**
 * Interceptor de OkHttp que añade automáticamente el token JWT a las cabeceras
 * de todas las peticiones de red que lo necesiten.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = SessionManager.jwtToken

        // Si tenemos un token, se crea una nueva petición con el header "Authorization"
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
