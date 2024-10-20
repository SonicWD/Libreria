package com.example.biblioteca.Screen.libro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.biblioteca.Model.Libro
import com.example.biblioteca.Repository.BibliotecaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow

class LibroViewModel(private val repository: BibliotecaRepository) : ViewModel() {
    val allLibros: StateFlow<List<Libro>> = repository.allLibros.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredLibros = combine(allLibros, searchQuery) { libros: List<Libro>, query: String ->
        if (query.isEmpty()) {
            libros
        } else {
            libros.filter {
                it.titulo.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addLibro(titulo: String, genero: String, autorId: Int) {
        viewModelScope.launch {
            val libro = Libro(
                titulo = titulo,
                genero = genero,
                autorId = autorId
            )
            repository.insertLibro(libro)
        }
    }

    fun updateLibro(libro: Libro) {
        viewModelScope.launch {
            repository.updateLibro(libro)
        }
    }

    fun deleteLibro(libro: Libro) {
        viewModelScope.launch {
            repository.deleteLibro(libro)
        }
    }

    fun getLibroById(id: Int) = repository.getLibroById(id)
}