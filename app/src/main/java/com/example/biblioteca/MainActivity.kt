import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biblioteca.Screen.autor.AutorScreen
import com.example.biblioteca.Screen.autor.AutorViewModel
import com.example.biblioteca.Screen.libro.LibroScreen
import com.example.biblioteca.Screen.libro.LibroViewModel
import com.example.biblioteca.Screen.miembro.MiembroScreen
import com.example.biblioteca.Screen.miembro.MiembroViewModel
import com.example.biblioteca.Screen.prestamo.PrestamoScreen
import com.example.biblioteca.Screen.prestamo.PrestamoViewModel
import com.example.biblioteca.ui.theme.BibliotecaTheme
import com.example.biblioteca.Repository.BibliotecaRepository

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BibliotecaRepository(context = this) // Assume this is properly initialized

        setContent {
            BibliotecaTheme {
                val navController = rememberNavController()
                var currentTitle by remember { mutableStateOf("Biblioteca") }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(currentTitle) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Book, contentDescription = "Libros") },
                                label = { Text("Libros") },
                                selected = currentTitle == "Libros",
                                onClick = {
                                    navController.navigate("libros")
                                    currentTitle = "Libros"
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Autores") },
                                label = { Text("Autores") },
                                selected = currentTitle == "Autores",
                                onClick = {
                                    navController.navigate("autores")
                                    currentTitle = "Autores"
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Group, contentDescription = "Miembros") },
                                label = { Text("Miembros") },
                                selected = currentTitle == "Miembros",
                                onClick = {
                                    navController.navigate("miembros")
                                    currentTitle = "Miembros"
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.SwapHoriz, contentDescription = "Préstamos") },
                                label = { Text("Préstamos") },
                                selected = currentTitle == "Préstamos",
                                onClick = {
                                    navController.navigate("prestamos")
                                    currentTitle = "Préstamos"
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "libros",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("libros") {
                            val viewModel: LibroViewModel = viewModel { LibroViewModel(repository) }
                            LibroScreen(viewModel = viewModel)
                        }
                        composable("autores") {
                            val viewModel: AutorViewModel = viewModel { AutorViewModel(repository) }
                            AutorScreen(viewModel = viewModel)
                        }
                        composable("miembros") {
                            val viewModel: MiembroViewModel = viewModel { MiembroViewModel(repository) }
                            MiembroScreen(viewModel = viewModel)
                        }
                        composable("prestamos") {
                            val viewModel: PrestamoViewModel = viewModel { PrestamoViewModel(repository) }
                            PrestamoScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}