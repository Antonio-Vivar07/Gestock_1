package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.navigation.Route
import kotlinx.coroutines.launch

data class MenuItem(val text: String, val icon: ImageVector, val action: () -> Unit)

@Composable
fun InventoryControlScreen(
    message: String?,
    onGoToRegister: () -> Unit,
    onGoToProductMenu: () -> Unit,
    onGoToProductEntry: () -> Unit,
    onGoToStock: () -> Unit,
    onGoToReports: () -> Unit,
    onGoToSearch: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- ESTADO PARA CONTROLAR EL MENSAJE DE UNA SOLA VEZ ---
    var messageShown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null && !messageShown) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                messageShown = true // Marca el mensaje como mostrado
            }
        }
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Éxito")
                        Spacer(Modifier.width(8.dp))
                        Text(data.visuals.message)
                    }
                }
            }
        }
    ) { innerPadding ->
        val bgColor = Color(0xFFE7F2FF)

        val menuItems = listOf(
            MenuItem("Crear usuario", Icons.Default.PersonAdd, onGoToRegister),
            MenuItem("Gestión de producto", Icons.Default.Category, onGoToProductMenu),
            MenuItem("Inventario", Icons.Default.Warehouse, onGoToProductEntry),
            MenuItem("Stock", Icons.Default.StackedLineChart, onGoToStock),
            MenuItem("Reporte de inventario", Icons.Default.Assessment, onGoToReports),
            MenuItem("Buscar producto", Icons.Default.Search, onGoToSearch)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().background(bgColor).padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems) {
                item ->
                InventoryButton(item = item, onClick = item.action)
            }
        }
    }
}

@Composable
private fun InventoryButton(item: MenuItem, onClick: () -> Unit) {
    val buttonColor = Color(0xFFE0D9FF)
    val textColor = Color(0xFF333333)

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier.height(130.dp).fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(imageVector = item.icon, contentDescription = item.text, modifier = Modifier.size(48.dp), tint = textColor)
            Spacer(Modifier.height(8.dp))
            Text(text = item.text, textAlign = TextAlign.Center, color = textColor)
        }
    }
}
