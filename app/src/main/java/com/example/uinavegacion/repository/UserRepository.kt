package com.example.uinavegacion.repository

import android.util.Log
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.LoginRequest
import com.example.uinavegacion.data.remote.RegisterRequest
import com.example.uinavegacion.data.remote.UserApiService
import com.example.uinavegacion.viewmodel.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserRepository(private val userDao: UserDao, private val userApiService: UserApiService) {

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun registerUser(username: String, email: String, pass: String, role: UserRole): Boolean {
        return try {
            val request = RegisterRequest(username, email, pass, role.name)
            val response = userApiService.registerUser(request)
            if (response.isSuccessful) {
                response.body()?.let { remoteUser ->
                    val userRole = UserRole.valueOf(remoteUser.role.uppercase())
                    // Al registrar, sí tenemos la contraseña
                    val localUser = UserEntity(username = remoteUser.username, email = remoteUser.email, pass = pass, role = userRole)
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
        return try {
            val request = LoginRequest(username, pass)
            val response = userApiService.loginUser(request)
            if (response.isSuccessful) {
                response.body()?.let { remoteUser ->
                    val userRole = UserRole.valueOf(remoteUser.role.uppercase())
                    var localUser = userDao.findByUsername(remoteUser.username)
                    if (localUser == null) {
                        // Si es el primer login, guardamos la contraseña que el usuario acaba de usar
                        localUser = UserEntity(username = remoteUser.username, email = remoteUser.email, pass = pass, role = userRole)
                    } else {
                        // Si ya existía, actualizamos sus datos pero mantenemos la pass local (que puede estar vacía o no)
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
            null
        }
    }
    
    suspend fun syncUsersWithBackend() {
        try {
            Log.d("UserRepository", "Iniciando sincronización de usuarios...")
            val remoteUsers = userApiService.getAllUsers()
            val localUsers = userDao.getAllUsers().first()

            val remoteUserMap = remoteUsers.associateBy { it.username }

            val usersToDelete = localUsers.filter { !remoteUserMap.containsKey(it.username) }
            for (user in usersToDelete) {
                userDao.delete(user)
                Log.d("UserRepository", "Usuario local '${user.username}' eliminado (ya no existe en el servidor).")
            }

            for (remoteUser in remoteUsers) {
                val localEquivalent = userDao.findByUsername(remoteUser.username)
                val userRole = UserRole.valueOf(remoteUser.role.uppercase())
                val entity = UserEntity(
                    id = localEquivalent?.id ?: 0,
                    username = remoteUser.username,
                    email = remoteUser.email,
                    pass = localEquivalent?.pass ?: "", // Usamos la contraseña local si existe, sino una vacía
                    role = userRole
                )
                userDao.insert(entity)
            }
            Log.d("UserRepository", "Sincronización de usuarios completada. Total de usuarios remotos: ${remoteUsers.size}")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error durante la sincronización de usuarios.", e)
            throw e
        }
    }
}
