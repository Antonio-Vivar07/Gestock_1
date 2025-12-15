package com.example.uinavegacion.data.local.database

import androidx.room.TypeConverter
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.viewmodel.UserRole

class Converters {
    // --- Convertidores para MovimientoType ---
    @TypeConverter
    fun fromMovimientoType(value: MovimientoType): String {
        return value.name
    }

    @TypeConverter
    fun toMovimientoType(value: String): MovimientoType {
        return MovimientoType.valueOf(value)
    }

    // --- Convertidores para UserRole (EL MANUAL QUE FALTABA) ---
    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }
}