package com.example.uinavegacion.data.local.movimiento

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.product.ProductEntity

enum class MovimientoType {
    INGRESO,
    EGRESO, 
    AJUSTE,
    TRANSFERENCIA
}

@Entity(
    tableName = "movimientos",
    foreignKeys = [ForeignKey(
        entity = ProductEntity::class,
        parentColumns = ["id"],
        childColumns = ["product_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "product_id", index = true)
    val productId: Int,

    @ColumnInfo(name = "type")
    val type: MovimientoType,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "reason")
    val reason: String?,

    @ColumnInfo(name = "reference_document")
    val referenceDocument: String?,

    @ColumnInfo(name = "user")
    val user: String,

    // --- CAMPO DE FECHA ACTUALIZADO ---
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
