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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole
import kotlinx.coroutines.launch

data class MenuItem(val text: String, val icon: ImageVector, val action: () -> Unit)

@Composable
fun InventoryControlScreen(
    message: String?,
    authVm: AuthViewModel,
    onGoToRegister: () -> Unit,
    onGoToProductMenu: () -> Unit,
    onGoToProductEntry: () -> Unit,
    onGoToStock: () -> Unit,
    onGoToReports: () -> Unit,
    onGoToSearch: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var messageShown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null && !messageShown) {
            scope.launch { snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short) }
            messageShown = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val session by authVm.session.collectAsState()
        val userRole = session?.role

        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f) // El grid de botones ocupa todo el espacio disponible
                    .background(Color(0xFFF0F4F8))
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val menuItems = listOf(
                    MenuItem("Ingresar producto", Icons.Default.AddBox, onGoToProductMenu),
                    MenuItem("Movimientos", Icons.Default.SyncAlt, onGoToProductEntry),
                    MenuItem("Inventario", Icons.Default.Inventory, onGoToStock),
                    MenuItem("Buscar / Escanear", Icons.Default.QrCodeScanner, onGoToSearch),
                    MenuItem("Reporte de inventario", Icons.Default.Assessment, onGoToReports),
                    MenuItem("Usuarios y roles", Icons.Default.Group, onGoToRegister)
                )

                val visibleMenuItems = if (userRole == UserRole.ADMINISTRADOR) {
                    menuItems
                } else {
                    menuItems.filter { it.text != "Usuarios y roles" }
                }

                items(visibleMenuItems) { item ->
                    InventoryButton(item = item, onClick = item.action)
                }
            }

            // --- ¡AQUÍ ESTÁ LA NUEVA SECCIÓN! ---
            session?.let {
                UserInfoBar(
                    role = it.role.name.replaceFirstChar { char -> char.titlecase() },
                    username = it.username
                )
            }
        }
    }
}

@Composable
private fun InventoryButton(item: MenuItem, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .height(130.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(imageVector = item.icon, contentDescription = item.text, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text = item.text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// --- ¡NUEVO COMPOSABLE PARA LA BARRA DE USUARIO! ---
@Composable
private fun UserInfoBar(role: String, username: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Usuario", tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$role: ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = username,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
