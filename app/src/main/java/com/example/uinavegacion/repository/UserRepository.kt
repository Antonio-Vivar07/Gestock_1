package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.RemoteUser
import com.example.uinavegacion.data.remote.RemoteUserLogin
import com.example.uinavegacion.data.remote.UpdateRoleRequest
import com.example.uinavegacion.data.remote.UserApiService
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.data.local.session.SessionManager
import com.example.uinavegacion.viewmodel.UserRole
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que maneja las operaciones de datos para los usuarios.
 * Es la única fuente de verdad para los datos de usuario.
 */
class UserRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    // Servicio remoto para hablar con el backend
    private val userApi: UserApiService = RetrofitClient.userApiService
) {

    /** ✅ NUEVO: usuarios locales desde Room */
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    /**
     * Lista usuarios desde el backend (requiere ADMIN).
     */
    suspend fun fetchRemoteUsers(): List<RemoteUser> {
        return userApi.getUsers()
    }

    /**
     * Cambia rol en backend (requiere ADMIN). Devuelve true si fue OK.
     */
    suspend fun updateRemoteUserRole(username: String, newRole: UserRole): Boolean {
        return try {
            userApi.updateUserRole(username, UpdateRoleRequest(role = newRole.name))
            true
        } catch (_: Exception) {
            false
        }
    }

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
        } catch (_: Exception) {
            // no rompemos app si falla backend
        }

        return true
    }

    /**
     * Valida las credenciales de un usuario.
     */
    suspend fun loginUser(username: String, pass: String): UserEntity? {
        return try {
            val loginRequest = RemoteUserLogin(
                username = username,
                password = pass
            )

            val response = userApi.loginUser(loginRequest)

            // Guardar sesión (JWT + user + rol)
            try {
                sessionManager.saveSession(response.jwt, response.username, response.role)
            } catch (_: Exception) { }

            val userRole = try {
                UserRole.valueOf(response.role)
            } catch (_: IllegalArgumentException) {
                UserRole.TRABAJADOR
            }

            val newLocalUser = UserEntity(
                username = response.username,
                email = response.email,
                pass = pass,
                role = userRole
            )

            userDao.insertUser(newLocalUser)
            newLocalUser
        } catch (e: HttpException) {
            if (e.code() == 401 || e.code() == 404) {
                try { userDao.deleteByUsername(username) } catch (_: Exception) {}
            }
            null
        } catch (_: Exception) {
            val localUser = userDao.findByUsername(username)
            if (localUser != null && localUser.pass == pass) localUser else null
        }
    }

    suspend fun clearSession() {
        sessionManager.clear()
    }
}
