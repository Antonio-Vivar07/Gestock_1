package com.example.uinavegacion.data.local.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    // --- ¡MÉTODO AÑADIDO QUE FALTABA! ---
    @Delete
    suspend fun delete(user: UserEntity)
}
