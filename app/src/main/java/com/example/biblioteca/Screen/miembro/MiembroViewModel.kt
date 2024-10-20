package com.example.biblioteca.Screen.miembro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca.Repository.BibliotecaRepository
import com.example.biblioteca.Model.Miembro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import java.util.Date

class MiembroViewModel(private val repository: BibliotecaRepository) : ViewModel() {
    val allMiembros = repository.allMiembros.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    val filteredMiembros = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) repository.allMiembros
        else repository.searchMiembros(query)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addMiembro(nombre: String, apellido: String) {
        viewModelScope.launch {
            val miembro = Miembro(
                nombre = nombre,
                apellido = apellido,
                fechaInscripcion = Date()
            )
            repository.insertMiembro(miembro)
        }
    }

    fun updateMiembro(miembro: Miembro) {
        viewModelScope.launch {
            repository.updateMiembro(miembro)
        }
    }

    fun deleteMiembro(miembro: Miembro) {
        viewModelScope.launch {
            repository.deleteMiembro(miembro)
        }
    }

    fun getMiembroById(id: Int) = repository.getMiembroById(id)

    val miembrosConPrestamosActivos = repository.miembrosConPrestamosActivos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
}