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
import com.example.biblioteca.Model.Prestamo
import com.example.biblioteca.Model.PrestamoConDetalles
import com.example.biblioteca.Screen.prestamo.PrestamoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoScreen(
    viewModel: PrestamoViewModel
) {
    val allPrestamos by viewModel.allPrestamos.collectAsStateWithLifecycle()
    var showRealizarPrestamoDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedPrestamo by remember { mutableStateOf<PrestamoConDetalles?>(null) }
    var showActiveLoansByMemberDialog by remember { mutableStateOf(false) }
    var selectedMiembroId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Préstamos") },
                actions = {
                    IconButton(onClick = { showRealizarPrestamoDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Realizar Préstamo")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allPrestamos) { prestamo ->
                PrestamoItem(
                    prestamo = prestamo,
                    onUpdate = {
                        selectedPrestamo = it
                        showUpdateDialog = true
                    },
                    onDelete = { viewModel.deletePrestamo(it.toPrestamo()) },
                    onViewDetails = {
                        selectedPrestamo = it
                        showDetailDialog = true
                    },
                    onDevolver = { viewModel.devolverLibro(it.prestamo_id) },
                    onViewActiveLoansByMember = {
                        selectedMiembroId = it.miembro_id
                        showActiveLoansByMemberDialog = true
                    }
                )
            }
        }

        if (showRealizarPrestamoDialog) {
            RealizarPrestamoDialog(
                onDismiss = { showRealizarPrestamoDialog = false },
                onConfirm = { libroId, miembroId ->
                    viewModel.realizarPrestamo(libroId, miembroId)
                    showRealizarPrestamoDialog = false
                }
            )
        }

        if (showUpdateDialog && selectedPrestamo != null) {
            UpdatePrestamoDialog(
                prestamo = selectedPrestamo!!,
                onDismiss = { showUpdateDialog = false },
                onConfirm = { prestamo ->
                    viewModel.updatePrestamo(prestamo.toPrestamo())
                    showUpdateDialog = false
                }
            )
        }

        if (showDetailDialog && selectedPrestamo != null) {
            PrestamoDetailDialog(
                prestamo = selectedPrestamo!!,
                onDismiss = { showDetailDialog = false }
            )
        }

        if (showActiveLoansByMemberDialog && selectedMiembroId != null) {
            ActiveLoansByMemberDialog(
                miembroId = selectedMiembroId!!,
                viewModel = viewModel,
                onDismiss = { showActiveLoansByMemberDialog = false }
            )
        }
    }
}

@Composable
fun PrestamoItem(
    prestamo: PrestamoConDetalles,
    onUpdate: (PrestamoConDetalles) -> Unit,
    onDelete: (PrestamoConDetalles) -> Unit,
    onViewDetails: (PrestamoConDetalles) -> Unit,
    onDevolver: (PrestamoConDetalles) -> Unit,
    onViewActiveLoansByMember: (PrestamoConDetalles) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Libro ID: ${prestamo.libro_id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Miembro: ${prestamo.miembro_nombre} ${prestamo.miembro_apellido}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fecha préstamo: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(prestamo.fecha_prestamo)}",
                style = MaterialTheme.typography.bodySmall
            )
            prestamo.fecha_devolucion?.let {
                Text(
                    text = "Fecha devolución: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onViewDetails(prestamo) }) {
                    Icon(Icons.Default.Info, contentDescription = "Ver detalles")
                }
                IconButton(onClick = { onUpdate(prestamo) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onDelete(prestamo) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
                if (prestamo.fecha_devolucion == null) {
                    IconButton(onClick = { onDevolver(prestamo) }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Devolver")
                    }
                }
                IconButton(onClick = { onViewActiveLoansByMember(prestamo) }) {
                    Icon(Icons.Default.Person, contentDescription = "Ver préstamos activos del miembro")
                }
            }
        }
    }
}

@Composable
fun RealizarPrestamoDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var libroId by remember { mutableStateOf("") }
    var miembroId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Realizar Préstamo") },
        text = {
            Column {
                OutlinedTextField(
                    value = libroId,
                    onValueChange = { libroId = it },
                    label = { Text("ID del Libro") }
                )
                OutlinedTextField(
                    value = miembroId,
                    onValueChange = { miembroId = it },
                    label = { Text("ID del Miembro") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(libroId.toIntOrNull() ?: 0, miembroId.toIntOrNull() ?: 0)
            }) {
                Text("Realizar Préstamo")
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
fun UpdatePrestamoDialog(
    prestamo: PrestamoConDetalles,
    onDismiss: () -> Unit,
    onConfirm: (PrestamoConDetalles) -> Unit
) {
    var libroId by remember { mutableStateOf(prestamo.libro_id.toString()) }
    var miembroId by remember { mutableStateOf(prestamo.miembro_id.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Préstamo") },
        text = {
            Column {
                OutlinedTextField(
                    value = libroId,
                    onValueChange = { libroId = it },
                    label = { Text("ID del Libro") }
                )
                OutlinedTextField(
                    value = miembroId,
                    onValueChange = { miembroId = it },
                    label = { Text("ID del Miembro") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(prestamo.copy(
                    libro_id = libroId.toIntOrNull() ?: prestamo.libro_id,
                    miembro_id = miembroId.toIntOrNull() ?: prestamo.miembro_id
                ))
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
fun PrestamoDetailDialog(
    prestamo: PrestamoConDetalles,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del Préstamo") },
        text = {
            Column {
                Text("Libro ID: ${prestamo.libro_id}")
                Text("Miembro: ${prestamo.miembro_nombre} ${prestamo.miembro_apellido}")
                Text("Fecha de Préstamo: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(prestamo.fecha_prestamo)}")
                prestamo.fecha_devolucion?.let {
                    Text("Fecha de Devolución: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)}")
                } ?: Text("No devuelto aún")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ActiveLoansByMemberDialog(
    miembroId: Int,
    viewModel: PrestamoViewModel,
    onDismiss: () -> Unit
) {
    val activeLoansByMemberState = viewModel.getPrestamosActivosByMiembro(miembroId).collectAsState(initial = emptyList())
    val activeLoans = activeLoansByMemberState.value

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Préstamos Activos del Miembro") },
        text = {
            LazyColumn {
                items(activeLoans) { prestamo ->
                    Text("Libro ID: ${prestamo.libro_id}, Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(prestamo.fecha_prestamo)}")
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

// Extension function to convert PrestamoConDetalles to Prestamo
fun PrestamoConDetalles.toPrestamo(): Prestamo {
    return Prestamo(
        prestamo_id = this.prestamo_id,
        libro_id = this.libro_id,
        miembro_id = this.miembro_id,
        fecha_prestamo = this.fecha_prestamo,
        fecha_devolucion = this.fecha_devolucion
    )
}