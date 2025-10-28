package com.example.uinavegacion.data.local.movimiento

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMovimiento(movimiento: MovimientoEntity)

    // Aquí, en el futuro, podrían ir funciones como:
    // @Query("SELECT * FROM movimientos WHERE product_id = :productId ORDER BY created_at DESC")
    // fun getMovimientosForProduct(productId: Int): Flow<List<MovimientoEntity>>
}
