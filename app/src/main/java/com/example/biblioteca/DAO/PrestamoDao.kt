package com.example.biblioteca.DAO

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.biblioteca.Model.Prestamo
import com.example.biblioteca.Model.PrestamoConDetalles

@Dao
interface PrestamoDao {

    // Obtener todos los préstamos
    @Query("SELECT * FROM prestamos")
    fun getAllPrestamos(): Flow<List<Prestamo>>

    // Obtener préstamos con detalles (libro y miembro)
    @Transaction
    @Query("""
        SELECT p.*, l.titulo as libro_titulo, m.nombre as miembro_nombre, m.apellido as miembro_apellido
        FROM prestamos p
        INNER JOIN libros l ON p.libro_id = l.libro_id
        INNER JOIN miembros m ON p.miembro_id = m.miembro_id
    """)
    fun getAllPrestamosConDetalles(): Flow<List<PrestamoConDetalles>>

    // Obtener préstamo por ID
    @Query("SELECT * FROM prestamos WHERE prestamo_id = :id")
    fun getPrestamoById(id: Int): Flow<Prestamo>

    // Insertar un nuevo préstamo
    @Insert
    suspend fun insert(prestamo: Prestamo): Long

    // Actualizar un préstamo existente
    @Update
    suspend fun update(prestamo: Prestamo)

    // Eliminar un préstamo
    @Delete
    suspend fun delete(prestamo: Prestamo)

    // Obtener préstamos activos (sin devolución) de un miembro
    @Query("""
        SELECT * FROM prestamos
        WHERE miembro_id = :miembroId
        AND fecha_devolucion IS NULL
    """)
    fun getPrestamosActivosByMiembro(miembroId: Int): Flow<List<Prestamo>>

    // Verificar si un libro está prestado (sin fecha de devolución)
    @Query("""
        SELECT COUNT(*) FROM prestamos
        WHERE libro_id = :libroId
        AND fecha_devolucion IS NULL
    """)
    suspend fun isLibroPrestado(libroId: Int): Int
}
