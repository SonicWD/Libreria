package com.example.biblioteca.Repository

import android.content.Context
import com.example.biblioteca.Database.BibliotecaDatabase
import com.example.biblioteca.Model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

class BibliotecaRepository(context: Context) {
    private val database: BibliotecaDatabase = BibliotecaDatabase.getDatabase(context)

    // Libros
    val allLibros: Flow<List<Libro>> = database.libroDao().getAllLibros()

    suspend fun insertLibro(libro: Libro) = database.libroDao().insert(libro)
    suspend fun updateLibro(libro: Libro) = database.libroDao().update(libro)
    suspend fun deleteLibro(libro: Libro) = database.libroDao().delete(libro)
    fun searchLibros(query: String) = database.libroDao().searchLibros(query)
    fun getLibroById(id: Int) = database.libroDao().getLibroById(id)

    // Autores
    val allAutores: Flow<List<Autor>> = database.autorDao().getAllAutores()

    suspend fun insertAutor(autor: Autor) = database.autorDao().insert(autor)
    suspend fun updateAutor(autor: Autor) = database.autorDao().update(autor)
    suspend fun deleteAutor(autor: Autor) = database.autorDao().delete(autor)
    fun searchAutores(query: String) = database.autorDao().searchAutores(query)
    fun getAutorById(id: Int) = database.autorDao().getAutorById(id)
    fun getAutoresByNacionalidad(nacionalidad: String) =
        database.autorDao().getAutoresByNacionalidad(nacionalidad)

    // Miembros
    val allMiembros: Flow<List<Miembro>> = database.miembroDao().getAllMiembros()
    val miembrosConPrestamosActivos = database.miembroDao().getMiembrosConPrestamosActivos()

    suspend fun insertMiembro(miembro: Miembro) = database.miembroDao().insert(miembro)
    suspend fun updateMiembro(miembro: Miembro) = database.miembroDao().update(miembro)
    suspend fun deleteMiembro(miembro: Miembro) = database.miembroDao().delete(miembro)
    fun searchMiembros(query: String) = database.miembroDao().searchMiembros(query)
    fun getMiembroById(id: Int) = database.miembroDao().getMiembroById(id)

    // Préstamos
    val allPrestamos: Flow<List<Prestamo>> = database.prestamoDao().getAllPrestamos()
    val allPrestamosConDetalles = database.prestamoDao().getAllPrestamosConDetalles()

    suspend fun insertPrestamo(prestamo: Prestamo): Long {
        // Validar que el libro no esté prestado
        val libroPrestado = database.prestamoDao().isLibroPrestado(prestamo.libro_id)
        if (libroPrestado > 0) {
            throw IllegalStateException("El libro ya está prestado")
        }
        return database.prestamoDao().insert(prestamo)
    }

    suspend fun updatePrestamo(prestamo: Prestamo) = database.prestamoDao().update(prestamo)
    suspend fun deletePrestamo(prestamo: Prestamo) = database.prestamoDao().delete(prestamo)
    fun getPrestamoById(id: Int) = database.prestamoDao().getPrestamoById(id)
    fun getPrestamosActivosByMiembro(miembroId: Int) =
        database.prestamoDao().getPrestamosActivosByMiembro(miembroId)

    suspend fun devolverLibro(prestamoId: Int, fechaDevolucion: Date) {
        val prestamo = database.prestamoDao().getPrestamoById(prestamoId).first()
        database.prestamoDao().update(prestamo.copy(fecha_devolucion = fechaDevolucion))
    }

    // Validaciones y utilidades
    suspend fun validarPrestamo(libroId: Int, miembroId: Int): Boolean {
        val prestamosActivos = database.prestamoDao().getPrestamosActivosByMiembro(miembroId).first()
        val libroPrestado = database.prestamoDao().isLibroPrestado(libroId)

        return prestamosActivos.size < 3 && libroPrestado == 0
    }
}