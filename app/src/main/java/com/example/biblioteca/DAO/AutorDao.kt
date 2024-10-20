package com.example.biblioteca.DAO

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow
import com.example.biblioteca.Model.Autor

@Dao
interface AutorDao {
    @Query("SELECT * FROM autores")
    fun getAllAutores(): Flow<List<Autor>>

    @Query("SELECT * FROM autores WHERE autor_id = :id")
    fun getAutorById(id: Int): Flow<Autor>

    @Insert
    suspend fun insert(autor: Autor): Long

    @Update
    suspend fun update(autor: Autor)

    @Delete
    suspend fun delete(autor: Autor)

    @Query("SELECT * FROM autores WHERE nombre LIKE '%' || :searchQuery || '%' OR apellido LIKE '%' || :searchQuery || '%'")
    fun searchAutores(searchQuery: String): Flow<List<Autor>>

    @Query("SELECT * FROM autores WHERE nacionalidad = :nacionalidad")
    fun getAutoresByNacionalidad(nacionalidad: String): Flow<List<Autor>>
}