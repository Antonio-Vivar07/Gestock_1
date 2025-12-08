package com.example.uinavegacion.repository

import android.util.Log
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.RemoteUser
import com.example.uinavegacion.data.remote.RemoteUserLogin
import com.example.uinavegacion.data.remote.UserApiService
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.viewmodel.UserRole

class UserRepository(
    private val userDao: UserDao,
    private val userApi: UserApiService = RetrofitClient.userApiService
) {

    suspend fun registerUser(username: String, email: String, pass: String, role: UserRole): Boolean {
        Log.d("UserRegistration", "--- INICIANDO REGISTRO PARA: $username ---")
        try {
            // PASO 1: VERIFICAR SI EL USUARIO EXISTE
            val existingUser = userDao.findByUsername(username)
            if (existingUser != null) {
                Log.e("UserRegistration", "FALLO: El usuario '$username' ya existe en la BD local. Abortando.")
                return false
            }
            Log.i("UserRegistration", "PASO 1 OK: El usuario '$username' no existe localmente.")

            // PASO 2: INTENTAR GUARDAR EN LA BD LOCAL
            val userEntity = UserEntity(
                username = username,
                email = email,
                pass = pass,
                role = role
            )
            userDao.insertUser(userEntity)
            Log.i("UserRegistration", "PASO 2 OK: Usuario '$username' insertado en la BD local.")

            // PASO 3: INTENTAR SINCRONIZAR CON EL BACKEND
            val remoteUser = RemoteUser(
                username = username,
                email = email,
                password = pass,
                role = role.name
            )
            try {
                userApi.registerUser(remoteUser)
                Log.i("UserRegistration", "PASO 3 OK: Usuario '$username' sincronizado con el backend.")
            } catch (e: Exception) {
                Log.w("UserRegistration", "AVISO: El registro local fue exitoso, pero falló la sincronización con el backend.", e)
            }

            Log.i("UserRegistration", "--- REGISTRO FINALIZADO CON ÉXITO PARA: $username ---")
            return true

        } catch (e: Exception) {
            // Si algo se rompe inesperadamente (el guardado local, etc.)
            Log.e("UserRegistration", "FALLO CATASTRÓFICO: El proceso de registro se ha roto inesperadamente.", e)
            return false
        }
    }

    suspend fun loginUser(username: String, pass: String): UserEntity? {
        val localUser = userDao.findByUsername(username)
        if (localUser != null && localUser.pass == pass) {
            return localUser
        }

        return try {
            val loginRequest = RemoteUserLogin(
                username = username,
                password = pass
            )
            val response = userApi.loginUser(loginRequest)

            val userRole = try {
                UserRole.valueOf(response.role)
            } catch (e: IllegalArgumentException) {
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
        } catch (e: Exception) {
            null
        }
    }
}
