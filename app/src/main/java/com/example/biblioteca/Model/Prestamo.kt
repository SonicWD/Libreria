package com.example.biblioteca.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "prestamos",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Libro::class,
            parentColumns = ["libro_id"],
            childColumns = ["libro_id"]
        ),
        ForeignKey(
            entity = Miembro::class,
            parentColumns = ["miembro_id"],
            childColumns = ["miembro_id"]
        )
    )
)
data class Prestamo(
    @PrimaryKey(autoGenerate = true)
    val prestamo_id: Int = 0,
    val libro_id: Int,
    val miembro_id: Int,
    val fecha_prestamo: Date,
    val fecha_devolucion: Date?
)