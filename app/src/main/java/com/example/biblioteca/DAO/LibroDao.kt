package com.example.biblioteca.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.biblioteca.Model.Libro
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {
    @Query("SELECT * FROM libros")
    fun getAllLibros(): Flow<List<Libro>>

    @Query("SELECT * FROM libros WHERE libro_id = :id")
    fun getLibroById(id: Int): Flow<Libro>

    @Insert
    suspend fun insert(libro: Libro): Long

    @Update
    suspend fun update(libro: Libro)

    @Delete
    suspend fun delete(libro: Libro)

    @Query("SELECT * FROM libros WHERE titulo LIKE '%' || :searchQuery || '%'")
    fun searchLibros(searchQuery: String): Flow<List<Libro>>
}
