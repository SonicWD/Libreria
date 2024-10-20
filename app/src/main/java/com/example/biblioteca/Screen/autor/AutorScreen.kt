package com.example.biblioteca.Screen.autor

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
import com.example.biblioteca.Model.Autor
import com.example.biblioteca.Screen.autor.AutorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutorScreen(
    viewModel: AutorViewModel
) {
    val allAutores by viewModel.allAutores.collectAsStateWithLifecycle()
    val filteredAutores by viewModel.filteredAutores.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedAutor by remember { mutableStateOf<Autor?>(null) }
    var nacionalidadFilter by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Autores") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Autor")
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
                placeholder = { Text("Buscar autores...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            OutlinedTextField(
                value = nacionalidadFilter,
                onValueChange = { nacionalidadFilter = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Filtrar por nacionalidad...") },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.getAutoresByNacionalidad(nacionalidadFilter)
                    }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(if (searchQuery.isEmpty() && nacionalidadFilter.isEmpty()) allAutores else filteredAutores) { autor ->
                    AutorItem(
                        autor = autor,
                        onUpdate = {
                            selectedAutor = it
                            showUpdateDialog = true
                        },
                        onDelete = { viewModel.deleteAutor(it) },
                        onViewDetails = {
                            selectedAutor = it
                            showDetailDialog = true
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddAutorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nombre: String, apellido: String, nacionalidad: String ->
                    viewModel.addAutor(nombre, apellido, nacionalidad)
                    showAddDialog = false
                }
            )
        }

        if (showUpdateDialog && selectedAutor != null) {
            UpdateAutorDialog(
                autor = selectedAutor!!,
                onDismiss = { showUpdateDialog = false },
                onConfirm = { autor: Autor ->
                    viewModel.updateAutor(autor)
                    showUpdateDialog = false
                }
            )
        }

        if (showDetailDialog && selectedAutor != null) {
            AutorDetailDialog(
                autorId = selectedAutor!!.autor_id,
                viewModel = viewModel,
                onDismiss = { showDetailDialog = false }
            )
        }
    }
}

@Composable
fun AutorItem(
    autor: Autor,
    onUpdate: (Autor) -> Unit,
    onDelete: (Autor) -> Unit,
    onViewDetails: (Autor) -> Unit
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
                    text = "${autor.nombre} ${autor.apellido}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = autor.nacionalidad,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onViewDetails(autor) }) {
                Icon(Icons.Default.Info, contentDescription = "Ver detalles")
            }
            IconButton(onClick = { onUpdate(autor) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(autor) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun AutorDetailDialog(
    autorId: Int,
    viewModel: AutorViewModel,
    onDismiss: () -> Unit
) {
    val autorState = viewModel.getAutorById(autorId).collectAsState(initial = null)
    val autor = autorState.value

    if (autor != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Detalles del Autor") },
            text = {
                Column {
                    Text("Nombre: ${autor.nombre}")
                    Text("Apellido: ${autor.apellido}")
                    Text("Nacionalidad: ${autor.nacionalidad}")
                    // Aquí puedes agregar más detalles si los tienes en tu modelo Autor
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

@Composable
fun AddAutorDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Autor") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") }
                )
                OutlinedTextField(
                    value = nacionalidad,
                    onValueChange = { nacionalidad = it },
                    label = { Text("Nacionalidad") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, apellido, nacionalidad) }) {
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
fun UpdateAutorDialog(
    autor: Autor,
    onDismiss: () -> Unit,
    onConfirm: (Autor) -> Unit
) {
    var nombre by remember { mutableStateOf(autor.nombre) }
    var apellido by remember { mutableStateOf(autor.apellido) }
    var nacionalidad by remember { mutableStateOf(autor.nacionalidad) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Autor") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = apellido,
                    onValueChange = { apellido = it },
                    label = { Text("Apellido") }
                )
                OutlinedTextField(
                    value = nacionalidad,
                    onValueChange = { nacionalidad = it },
                    label = { Text("Nacionalidad") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(autor.copy(nombre = nombre, apellido = apellido, nacionalidad = nacionalidad)) }) {
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