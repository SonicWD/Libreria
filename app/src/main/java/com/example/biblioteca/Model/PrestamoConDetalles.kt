package com.example.biblioteca.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.Date

@Entity(tableName = "prestamo_con_detalles")
data class PrestamoConDetalles(
    @ColumnInfo(name = "prestamo_id") val prestamo_id: Int,
    @ColumnInfo(name = "libro_id") val libro_id: Int,
    @ColumnInfo(name = "miembro_id") val miembro_id: Int,
    @ColumnInfo(name = "fecha_prestamo") val fecha_prestamo: Date,
    @ColumnInfo(name = "fecha_devolucion") val fecha_devolucion: Date?,
    @ColumnInfo(name = "libro_titulo") val libro_titulo: String,
    @ColumnInfo(name = "miembro_nombre") val miembro_nombre: String,
    @ColumnInfo(name = "miembro_apellido") val miembro_apellido: String
)