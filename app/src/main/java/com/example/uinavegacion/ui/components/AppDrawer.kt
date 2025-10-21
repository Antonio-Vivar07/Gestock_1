package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.navigation.Route

data class DrawerItem(val icon: ImageVector, val label: String, val route: String, val onClick: () -> Unit)

fun defaultDrawerItems(
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGoToProductMenu: () -> Unit,
    onGoToProductEntry: () -> Unit,
    onGoToReports: () -> Unit,
    onGoToSearch: () -> Unit,
    onLogout: () -> Unit
): List<DrawerItem> {
    return if (isLoggedIn) {
        // Opciones para cuando el usuario TIENE sesión
        listOf(
            DrawerItem(Icons.Default.Home, "Home", Route.InventoryControl.path, onHome), // "Home" ahora es el control de inventario
            DrawerItem(Icons.Default.Add, "Crear usuario", Route.Register.path, onRegister),
            DrawerItem(Icons.Default.Category, "Gestión de producto", Route.ProductMenu.path, onGoToProductMenu),
            DrawerItem(Icons.Default.Warehouse, "Inventario", Route.ProductEntry.path, onGoToProductEntry),
            DrawerItem(Icons.Default.Search, "Buscar producto", Route.SearchProduct.path, onGoToSearch),
            DrawerItem(Icons.Default.Assessment, "Reporte", Route.ReportsDashboard.path, onGoToReports),
            DrawerItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión", "logout", onLogout)
        )
    } else {
        // Opciones para cuando el usuario NO tiene sesión
        listOf(
            DrawerItem(Icons.Default.Home, "Home", Route.Home.path, onHome),
            DrawerItem(Icons.Default.Login, "Login", Route.Login.path, onLogin),
            DrawerItem(Icons.Default.PersonAdd, "Registro", Route.Register.path, onRegister)
        )
    }
}

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            if (item.label == "Cerrar Sesión") {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = item.onClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}