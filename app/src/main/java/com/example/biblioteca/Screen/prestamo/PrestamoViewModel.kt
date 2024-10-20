package com.example.biblioteca.Screen.prestamo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca.Repository.BibliotecaRepository
import com.example.biblioteca.Model.Prestamo
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.util.Date

class PrestamoViewModel(private val repository: BibliotecaRepository) : ViewModel() {
    val allPrestamos = repository.allPrestamosConDetalles.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun realizarPrestamo(libroId: Int, miembroId: Int) {
        viewModelScope.launch {
            try {
                if (repository.validarPrestamo(libroId, miembroId)) {
                    val prestamo = Prestamo(
                        libro_id = libroId,
                        miembro_id = miembroId,
                        fecha_prestamo = Date(),
                        fecha_devolucion = null
                    )
                    repository.insertPrestamo(prestamo)
                }
            } catch (e: IllegalStateException) {
                // Manejar error
            }
        }
    }

    fun devolverLibro(prestamoId: Int) {
        viewModelScope.launch {
            repository.devolverLibro(prestamoId, Date())
        }
    }

    fun updatePrestamo(prestamo: Prestamo) {
        viewModelScope.launch {
            repository.updatePrestamo(prestamo)
        }
    }

    fun deletePrestamo(prestamo: Prestamo) {
        viewModelScope.launch {
            repository.deletePrestamo(prestamo)
        }
    }

    fun getPrestamoById(id: Int) = repository.getPrestamoById(id)

    fun getPrestamosActivosByMiembro(miembroId: Int) = repository.getPrestamosActivosByMiembro(miembroId)
}