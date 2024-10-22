package com.example.biblioteca.DAO

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import com.example.biblioteca.Model.Miembro
import com.example.biblioteca.Model.MiembroConPrestamosActivos

@Dao
interface MiembroDao {
    @Query("SELECT * FROM miembros")
    fun getAllMiembros(): Flow<List<Miembro>>

    @Query("SELECT * FROM miembros WHERE miembro_id = :id")
    fun getMiembroById(id: Int): Flow<Miembro>

    @Insert
    suspend fun insert(miembro: Miembro): Long

    @Update
    suspend fun update(miembro: Miembro)

    @Delete
    suspend fun delete(miembro: Miembro)

    @Query("SELECT * FROM miembros WHERE nombre LIKE '%' || :searchQuery || '%' OR apellido LIKE '%' || :searchQuery || '%'")
    fun searchMiembros(searchQuery: String): Flow<List<Miembro>>

    @Query("""
        SELECT m.miembro_id, m.nombre, m.apellido, m.fecha_inscripcion, COUNT(p.prestamo_id) as prestamos_activos
        FROM miembros m
        LEFT JOIN prestamos p ON m.miembro_id = p.miembro_id
        WHERE p.fecha_devolucion IS NULL
        GROUP BY m.miembro_id
    """)
    fun getMiembrosConPrestamosActivos(): Flow<List<MiembroConPrestamosActivos>>
}