package com.example.biblioteca.Model

data class MiembroConPrestamosActivos(
    val miembro_id: Int,
    val nombre: String,
    val apellido: String,
    val fecha_inscripcion: String,
    val prestamos_activos: Int
)