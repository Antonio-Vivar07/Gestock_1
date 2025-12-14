package com.example.uinavegacion.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    /**
     * Inserta un nuevo usuario en la tabla. Si el nombre de usuario ya existe,
     * la operación se aborta gracias a OnConflictStrategy.ABORT.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    /**
     * Busca un usuario por su nombre de usuario.
     * Devuelve el usuario si se encuentra, o null si no existe.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteByUsername(username: String)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
