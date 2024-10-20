package com.example.biblioteca.Model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "miembros")
data class Miembro(
    @PrimaryKey(autoGenerate = true)
    val miembro_id: Int = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "apellido")
    val apellido: String,
    @ColumnInfo(name = "fecha_inscripcion")
    val fechaInscripcion: Date
)