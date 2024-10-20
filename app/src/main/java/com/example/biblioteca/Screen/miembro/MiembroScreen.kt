package com.example.biblioteca.Screen.miembro

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
import com.example.biblioteca.Model.Miembro
import com.example.biblioteca.Screen.miembro.MiembroViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiembroScreen(
    viewModel: MiembroViewModel
) {
    val allMiembros by viewModel.allMiembros.collectAsStateWithLifecycle()
    val filteredMiembros by viewModel.filteredMiembros.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val miembrosConPrestamosActivos by viewModel.miembrosConPrestamosActivos.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedMiembro by remember { mutableStateOf<Miembro?>(null) }
    var showActiveLoansMembersDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Miembros") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Miembro")
                    }
                    IconButton(onClick = { showActiveLoansMembersDialog = true }) {
                        Icon(Icons.Default.Book, contentDescription = "Miembros con préstamos activos")
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
                placeholder = { Text("Buscar miembros...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(if (searchQuery.isEmpty()) allMiembros else filteredMiembros) { miembro ->
                    MiembroItem(
                        miembro = miembro,
                        onUpdate = {
                            selectedMiembro = it
                            showUpdateDialog = true
                        },
                        onDelete = { viewModel.deleteMiembro(it) },
                        onViewDetails = {
                            selectedMiembro = it
                            showDetailDialog = true
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddMiembroDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nombre, apellido ->
                    viewModel.addMiembro(nombre, apellido)
                    showAddDialog = false
                }
            )
        }

        if (showUpdateDialog && selectedMiembro != null) {
            UpdateMiembroDialog(
                miembro = selectedMiembro!!,
                onDismiss = { showUpdateDialog = false },
                onConfirm = { miembro ->
                    viewModel.updateMiembro(miembro)
                    showUpdateDialog = false
                }
            )
        }

        if (showDetailDialog && selectedMiembro != null) {
            MiembroDetailDialog(
                miembroId = selectedMiembro!!.miembro_id,
                viewModel = viewModel,
                onDismiss = { showDetailDialog = false }
            )
        }

        if (showActiveLoansMembersDialog) {
            ActiveLoansMembersDialog(
                miembros = miembrosConPrestamosActivos,
                onDismiss = { showActiveLoansMembersDialog = false }
            )
        }
    }
}

@Composable
fun MiembroItem(
    miembro: Miembro,
    onUpdate: (Miembro) -> Unit,
    onDelete: (Miembro) -> Unit,
    onViewDetails: (Miembro) -> Unit
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
                    text = "${miembro.nombre} ${miembro.apellido}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Inscrito: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(miembro.fechaInscripcion)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onViewDetails(miembro) }) {
                Icon(Icons.Default.Info, contentDescription = "Ver detalles")
            }
            IconButton(onClick = { onUpdate(miembro) }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(miembro) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun AddMiembroDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Miembro") },
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
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, apellido) }) {
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
fun UpdateMiembroDialog(
    miembro: Miembro,
    onDismiss: () -> Unit,
    onConfirm: (Miembro) -> Unit
) {
    var nombre by remember { mutableStateOf(miembro.nombre) }
    var apellido by remember { mutableStateOf(miembro.apellido) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Miembro") },
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
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(miembro.copy(nombre = nombre, apellido = apellido))
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
fun MiembroDetailDialog(
    miembroId: Int,
    viewModel: MiembroViewModel,
    onDismiss: () -> Unit
) {
    val miembroState = viewModel.getMiembroById(miembroId).collectAsState(initial = null)
    val miembro = miembroState.value

    if (miembro != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Detalles del Miembro") },
            text = {
                Column {
                    Text("Nombre: ${miembro.nombre}")
                    Text("Apellido: ${miembro.apellido}")
                    Text("Fecha de Inscripción: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(miembro.fechaInscripcion)}")
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
fun ActiveLoansMembersDialog(
    miembros: List<Miembro>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Miembros con Préstamos Activos") },
        text = {
            LazyColumn {
                items(miembros) { miembro ->
                    Text("${miembro.nombre} ${miembro.apellido}")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}