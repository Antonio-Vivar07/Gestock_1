package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DrawerItem(val icon: ImageVector, val text: String, val onClick: () -> Unit)

// --- FIRMA DE LA FUNCIÓN CORREGIDA Y COMPLETADA ---
@Composable
fun defaultDrawerItems(
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGoToCreateProduct: () -> Unit,
    onGoToMovements: () -> Unit,
    onGoToInventoryList: () -> Unit,
    onGoToSearchAndScan: () -> Unit,
    onGoToReports: () -> Unit,
    onGoToRemotePosts: () -> Unit,
    onLogout: () -> Unit
): List<DrawerItem> {
    return if (isLoggedIn) {
        listOf(
            DrawerItem(Icons.Default.Home, "Inicio", onHome),
            DrawerItem(Icons.Default.AddBox, "Ingresar Producto", onGoToCreateProduct),
            DrawerItem(Icons.Default.SyncAlt, "Movimientos", onGoToMovements),
            DrawerItem(Icons.Default.Inventory, "Inventario", onGoToInventoryList),
            DrawerItem(Icons.Default.Search, "Buscar / Escanear", onGoToSearchAndScan),
            DrawerItem(Icons.Default.Assessment, "Reportes", onGoToReports),
            DrawerItem(Icons.Default.Cloud, "API de prueba", onGoToRemotePosts),
            DrawerItem(Icons.Default.Group, "Usuarios", onRegister),
            DrawerItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión", onLogout)
        )
    } else {
        listOf(
            DrawerItem(Icons.Default.Home, "Inicio", onHome),
            DrawerItem(Icons.AutoMirrored.Filled.Login, "Iniciar Sesión", onLogin),
            DrawerItem(Icons.Default.PersonAdd, "Registro", onRegister)
        )
    }
}

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            items.forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.text) },
                    selected = currentRoute == item.text,
                    onClick = item.onClick,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
