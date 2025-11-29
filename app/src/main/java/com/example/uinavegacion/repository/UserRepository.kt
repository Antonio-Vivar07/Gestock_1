package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.RemoteUser
import com.example.uinavegacion.data.remote.RemoteUserLogin
import com.example.uinavegacion.data.remote.UserApiService
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.viewmodel.UserRole

/**
 * Repositorio que maneja las operaciones de datos para los usuarios.
 * Es la única fuente de verdad para los datos de usuario.
 */
class UserRepository(
    private val userDao: UserDao,
    // Servicio remoto para hablar con el backend
    private val userApi: UserApiService = RetrofitClient.userApiService
) {

    /**
     * Registra un nuevo usuario en la base de datos local
     * y además lo envía al backend (MongoDB) mediante la API REST.
     *
     * Devuelve true si el registro fue exitoso, false si el usuario ya existía.
     */
    suspend fun registerUser(
        username: String,
        email: String,
        pass: String,
        role: UserRole
    ): Boolean {

        // 1) Validación local: ¿ya existe el usuario?
        if (userDao.findByUsername(username) != null) {
            return false // El usuario ya existe
        }

        // 2) Guardar localmente en Room
        val userEntity = UserEntity(
            username = username,
            email = email,
            pass = pass,
            role = role
        )
        userDao.insertUser(userEntity)

        // 3) Enviar al backend (MongoDB)
        val remoteUser = RemoteUser(
            username = username,
            email = email,
            password = pass,
            role = role.name  // ADMINISTRADOR / TRABAJADOR
        )

        try {
            userApi.registerUser(remoteUser)
        } catch (e: Exception) {
            // Si falla la llamada al backend, NO rompemos la app.
            // Puedes agregar logs si quieres.
            // Log.e("UserRepository", "Error registrando usuario remoto", e)
        }

        return true
    }

    /**
     * Valida las credenciales de un usuario.
     *
     * Primero intenta validar contra la BD local (Room).
     * Si no encuentra nada o las credenciales no coinciden,
     * intenta hacer login en el backend.
     *
     * Devuelve la entidad del usuario si es válido, o null si no lo es.
     */
    suspend fun loginUser(username: String, pass: String): UserEntity? {
        // 1) Intentar con la base de datos local
        val localUser = userDao.findByUsername(username)
        if (localUser != null && localUser.pass == pass) {
            return localUser
        }

        // 2) Si localmente no está, intentamos con el backend
        return try {
            val loginRequest = RemoteUserLogin(
                username = username,
                password = pass
            )

            val response = userApi.loginUser(loginRequest)

            // Mapear la respuesta remota a una entidad local
            val userRole = try {
                UserRole.valueOf(response.role)
            } catch (e: IllegalArgumentException) {
                UserRole.TRABAJADOR // Valor por defecto por si llega algo raro
            }

            val newLocalUser = UserEntity(
                username = response.username,
                email = response.email,
                pass = pass,     // guardamos la misma pass usada en el login
                role = userRole
            )

            // Guardamos / actualizamos en Room para futuros logins offline
            userDao.insertUser(newLocalUser)

            newLocalUser
        } catch (e: Exception) {
            // Si el backend falla o devuelve error de credenciales, devolvemos null
            null
        }
    }
}
