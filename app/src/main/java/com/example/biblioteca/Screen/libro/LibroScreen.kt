package com.example.biblioteca.Screen.libro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.biblioteca.Model.Libro
import com.example.biblioteca.Screen.libro.LibroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibroScreen(
    viewModel: LibroViewModel
) {
    val allLibros by viewModel.allLibros.collectAsStateWithLifecycle()
    val filteredLibros by viewModel.filteredLibros.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedLibro by remember { mutableStateOf<Libro?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Libros") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Libro")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar libros...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(if (searchQuery.isEmpty()) allLibros else filteredLibros) { libro ->
                    LibroItem(
                        libro = libro,
                        onUpdate = {
                            selectedLibro = it
                            showUpdateDialog = true
                        },
                        onDelete = { viewModel.deleteLibro(it) },
                        onViewDetails = {
                            selectedLibro = it
                            showDetailDialog = true
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddLibroDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { titulo, genero, autorId ->
                    viewModel.addLibro(titulo, genero, autorId)
                    showAddDialog = false
                }
            )
        }

        if (showUpdateDialog && selectedLibro != null) {
            UpdateLibroDialog(
                libro = selectedLibro!!,
                onDismiss = { showUpdateDialog = false },
                onConfirm = { libro ->
                    viewModel.updateLibro(libro)
                    showUpdateDialog = false
                }
            )
        }

        if (showDetailDialog && selectedLibro != null) {
            LibroDetailDialog(
                libroId = selectedLibro!!.libro_id,
                viewModel = viewModel,
                onDismiss = { showDetailDialog = false }
            )
        }
    }
}

@Composable
fun LibroItem(
    libro: Libro,
    onUpdate: (Libro) -> Unit,
    onDelete: (Libro) -> Unit,
    onViewDetails: (Libro) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = libro.genero,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onViewDetails(libro) }) {
                Icon(Icons.Default.Info, contentDescription = "Ver detalles")
            }
            IconButton(onClick = { onUpdate(libro) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(libro) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun AddLibroDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var autorId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Libro") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") }
                )
                OutlinedTextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") }
                )
                OutlinedTextField(
                    value = autorId,
                    onValueChange = { autorId = it },
                    label = { Text("ID del Autor") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(titulo, genero, autorId.toIntOrNull() ?: 0)
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UpdateLibroDialog(
    libro: Libro,
    onDismiss: () -> Unit,
    onConfirm: (Libro) -> Unit
) {
    var titulo by remember { mutableStateOf(libro.titulo) }
    var genero by remember { mutableStateOf(libro.genero) }
    var autorId by remember { mutableStateOf(libro.autorId.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Libro") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") }
                )
                OutlinedTextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") }
                )
                OutlinedTextField(
                    value = autorId,
                    onValueChange = { autorId = it },
                    label = { Text("ID del Autor") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(libro.copy(titulo = titulo, genero = genero, autorId = autorId.toIntOrNull() ?: libro.autorId))
            }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun LibroDetailDialog(
    libroId: Int,
    viewModel: LibroViewModel,
    onDismiss: () -> Unit
) {
    val libroState = viewModel.getLibroById(libroId).collectAsState(initial = null)
    val libro = libroState.value

    if (libro != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Detalles del Libro") },
            text = {
                Column {
                    Text("Título: ${libro.titulo}")
                    Text("Género: ${libro.genero}")
                    Text("ID del Autor: ${libro.autorId}")
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        )
    }
}