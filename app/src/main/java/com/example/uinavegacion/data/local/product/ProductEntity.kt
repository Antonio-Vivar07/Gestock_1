package com.example.uinavegacion.data.local.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // --- Campos existentes ---
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "zone")
    val zone: String,

    @ColumnInfo(name = "min_stock")
    val minStock: Int = 0,

    @ColumnInfo(name = "stock")
    val stock: Int = 0,

    @ColumnInfo(name = "last_update")
    val lastUpdate: Long = 0L,

    @ColumnInfo(name = "status")
    val status: String = "ACTIVE",

    // --- CAMPO AÑADIDO PARA GUARDAR EL ID DEL SERVIDOR ---
    @ColumnInfo(name = "remote_id")
    val remoteId: String? = null
)
