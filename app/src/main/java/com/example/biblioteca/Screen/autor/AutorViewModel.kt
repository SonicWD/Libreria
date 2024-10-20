package com.example.biblioteca.Screen.autor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca.Repository.BibliotecaRepository
import com.example.biblioteca.Model.Autor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class AutorViewModel(private val repository: BibliotecaRepository) : ViewModel() {
    val allAutores = repository.allAutores.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    val filteredAutores = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) repository.allAutores
        else repository.searchAutores(query)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addAutor(nombre: String, apellido: String, nacionalidad: String) {
        viewModelScope.launch {
            val autor = Autor(
                nombre = nombre,
                apellido = apellido,
                nacionalidad = nacionalidad
            )
            repository.insertAutor(autor)
        }
    }

    fun updateAutor(autor: Autor) {
        viewModelScope.launch {
            repository.updateAutor(autor)
        }
    }

    fun deleteAutor(autor: Autor) {
        viewModelScope.launch {
            repository.deleteAutor(autor)
        }
    }

    fun getAutorById(id: Int) = repository.getAutorById(id)

    fun getAutoresByNacionalidad(nacionalidad: String) = repository.getAutoresByNacionalidad(nacionalidad)
}