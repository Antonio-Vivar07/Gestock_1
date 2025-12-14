package com.example.uinavegacion.repository

import android.util.Log
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.LoginRequest
import com.example.uinavegacion.data.remote.RegisterRequest
import com.example.uinavegacion.data.remote.UpdateRoleRequest
import com.example.uinavegacion.data.remote.UserApiService
<<<<<<< Updated upstream
import com.example.uinavegacion.viewmodel.SessionManager
import com.example.uinavegacion.viewmodel.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(private val userDao: UserDao, private val userApiService: UserApiService) {
=======
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.data.local.session.SessionManager
import com.example.uinavegacion.viewmodel.UserRole
import retrofit2.HttpException

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
>>>>>>> Stashed changes

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun registerUser(username: String, email: String, pass: String, role: UserRole): Boolean {
        return try {
            val request = RegisterRequest(username, email, pass, role.name)
            val response = userApiService.registerUser(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    val userRole = UserRole.valueOf(it.role.uppercase())
                    val localUser = UserEntity(username = it.username, email = it.email, pass = pass, role = userRole)
                    userDao.insert(localUser)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en el registro: ", e)
            false
        }
    }

    suspend fun loginUser(username: String, pass: String): UserEntity? {
<<<<<<< Updated upstream
=======
        // Primero intentamos con el backend para evitar conflictos:
        // - si borraste el usuario en MongoDB, aquí se detecta y limpiamos Room.
>>>>>>> Stashed changes
        return try {
            val request = LoginRequest(username, pass)
            val response = userApiService.loginUser(request)
            if (response.isSuccessful) {
                response.body()?.let { remoteUser ->
                    // --- ¡CORRECCIÓN! Se guarda el token JWT en la sesión ---
                    SessionManager.jwtToken = remoteUser.jwt

<<<<<<< Updated upstream
                    val userRole = UserRole.valueOf(remoteUser.role.uppercase())
                    var localUser = userDao.findByUsername(remoteUser.username)
                    if (localUser == null) {
                        localUser = UserEntity(username = remoteUser.username, email = remoteUser.email, pass = pass, role = userRole)
                    } else {
                        localUser = localUser.copy(email = remoteUser.email, role = userRole)
                    }
                    userDao.insert(localUser)
                    return@let localUser
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en el login: ", e)
=======
            val response = userApi.loginUser(loginRequest)

            // Guardar sesión (JWT + user + rol) para que Retrofit agregue headers
            try {
                sessionManager.saveSession(response.jwt, response.username, response.role)
            } catch (_: Exception) { }


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
        } catch (e: HttpException) {
            // Si el backend rechaza credenciales o el usuario ya no existe,
            // limpiamos el usuario local para que no quede "fantasma".
            if (e.code() == 401 || e.code() == 404) {
                try { userDao.deleteByUsername(username) } catch (_: Exception) {}
            }
>>>>>>> Stashed changes
            null
        } catch (e: Exception) {
            // Si el backend está caído, último recurso: permitir login local.
            val localUser = userDao.findByUsername(username)
            if (localUser != null && localUser.pass == pass) localUser else null
        }
    }

<<<<<<< Updated upstream
    suspend fun syncUsersWithBackend() {
        try {
            Log.d("UserRepository", "Iniciando sincronización de usuarios...")
            val remoteUsers = userApiService.getAllUsers()
            val localUsers = userDao.getAllUsers().first()

            val remoteUserMap = remoteUsers.associateBy { it.username }

            val usersToDelete = localUsers.filter { !remoteUserMap.containsKey(it.username) }
            for (user in usersToDelete) {
                userDao.delete(user)
                Log.d("UserRepository", "Usuario local '${user.username}' eliminado.")
            }

            for (remoteUser in remoteUsers) {
                val localEquivalent = userDao.findByUsername(remoteUser.username)
                val userRole = UserRole.valueOf(remoteUser.role.uppercase())
                val entity = UserEntity(
                    id = localEquivalent?.id ?: 0,
                    username = remoteUser.username,
                    email = remoteUser.email,
                    pass = localEquivalent?.pass ?: "",
                    role = userRole
                )
                userDao.insert(entity)
            }
            Log.d("UserRepository", "Sincronización de usuarios completada.")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error durante la sincronización de usuarios.", e)
            throw e
        }
    }

    suspend fun updateUserRole(user: UserEntity, newRole: UserRole): Boolean {
        return try {
            val request = UpdateRoleRequest(role = newRole.name)
            val response = userApiService.updateUserRole(user.username, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
=======

    suspend fun clearSession() {
        sessionManager.clear()
>>>>>>> Stashed changes
    }
}
