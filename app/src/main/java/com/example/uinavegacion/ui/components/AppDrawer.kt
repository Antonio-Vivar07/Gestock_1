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
import com.example.uinavegacion.viewmodel.UserRole

data class DrawerItem(val icon: ImageVector, val text: String, val onClick: () -> Unit)

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

/**
 * Items del Drawer según sesión y rol.
 * - TRABAJADOR: no ve "Usuarios y Roles".
 * - ADMINISTRADOR: ve todo.
 */
@Composable
fun defaultDrawerItems(
    isLoggedIn: Boolean,
    role: UserRole?,
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

    if (!isLoggedIn) {
        return listOf(
            DrawerItem(Icons.Default.Home, "Inicio", onHome),
            DrawerItem(Icons.AutoMirrored.Filled.Login, "Iniciar Sesión", onLogin),
            DrawerItem(Icons.Default.PersonAdd, "Registrar Usuario", onRegister)
        )
    }

    val items = mutableListOf(
        DrawerItem(Icons.Default.Home, "Inicio", onHome),
        DrawerItem(Icons.Default.AddBox, "Ingresar Producto", onGoToCreateProduct),
        DrawerItem(Icons.Default.SyncAlt, "Movimientos", onGoToMovements),
        DrawerItem(Icons.Default.Inventory, "Inventario", onGoToInventoryList),
        DrawerItem(Icons.Default.Search, "Buscar / Escanear", onGoToSearchAndScan),
        DrawerItem(Icons.Default.Assessment, "Reportes", onGoToReports),
        DrawerItem(Icons.Default.Cloud, "API de prueba", onGoToRemotePosts),
    )

    if (role == UserRole.ADMINISTRADOR) {
        items.add(DrawerItem(Icons.Default.Group, "Usuarios y Roles", onRegister))
    }

    items.add(DrawerItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión", onLogout))
    return items
}
