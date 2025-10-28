package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.viewmodel.UserRole

/**
 * Repositorio que maneja las operaciones de datos para los usuarios.
 * Es la única fuente de verdad para los datos de usuario.
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Registra un nuevo usuario en la base de datos.
     * Devuelve true si el registro fue exitoso, false si el usuario ya existía.
     */
    suspend fun registerUser(username: String, email: String, pass: String, role: UserRole): Boolean {
        // Primero, comprobamos si el usuario ya existe para evitar errores de la base de datos.
        if (userDao.findByUsername(username) != null) {
            return false // El usuario ya existe
        }

        val userEntity = UserEntity(
            username = username,
            email = email,
            pass = pass,
            role = role
        )
        userDao.insertUser(userEntity)
        return true
    }

    /**
     * Valida las credenciales de un usuario contra la base de datos.
     * Devuelve la entidad del usuario si es válido, o null si no lo es.
     */
    suspend fun loginUser(username: String, pass: String): UserEntity? {
        val user = userDao.findByUsername(username)
        // Devuelve el usuario solo si se encontró y la contraseña coincide.
        return if (user != null && user.pass == pass) {
            user
        } else {
            null
        }
    }
}
