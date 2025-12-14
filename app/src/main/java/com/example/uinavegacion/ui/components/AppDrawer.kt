package com.example.uinavegacion.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
<<<<<<< Updated upstream
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.uinavegacion.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    onOpenDrawer: (() -> Unit)?,
    authVm: AuthViewModel
) {
    val session by authVm.session.collectAsState()

    TopAppBar(
        title = {
            Column {
                Text(text = title)
                session?.let {
                    Text(
                        text = "${it.role.name.lowercase().replaceFirstChar { char -> char.titlecase() }}: ${it.username}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.White // Texto en blanco para que contraste
                    )
                }
            }
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            } else if (onOpenDrawer != null) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                }
            }
        },
        // --- ¡CORRECCIÓN! Se fuerza el color de fondo ---
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        )
    )
}

data class DrawerItem(
    val icon: ImageVector,
    val text: String,
    val route: String,
    val action: () -> Unit
)
=======
import com.example.uinavegacion.viewmodel.UserRole

data class DrawerItem(val icon: ImageVector, val text: String, val onClick: () -> Unit)
>>>>>>> Stashed changes

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>
) {
    ModalDrawerSheet {
<<<<<<< Updated upstream
        Spacer(Modifier.height(12.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.text) },
                selected = currentRoute == item.route,
                onClick = item.action,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

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
    onLogout: () -> Unit
): List<DrawerItem> {
    val itemList = mutableListOf<DrawerItem>()
    if (isLoggedIn) {
        itemList.add(DrawerItem(Icons.Default.Home, "Inicio", "inventory_control", onHome))
        itemList.add(DrawerItem(Icons.Default.AddBox, "Ingresar Producto", "create_product", onGoToCreateProduct))
        itemList.add(DrawerItem(Icons.Default.SyncAlt, "Movimientos de Stock", "movements", onGoToMovements))
        itemList.add(DrawerItem(Icons.Default.Inventory, "Inventario", "inventory_list", onGoToInventoryList))
        itemList.add(DrawerItem(Icons.Default.QrCodeScanner, "Buscar / Escanear", "search_scan", onGoToSearchAndScan))
        itemList.add(DrawerItem(Icons.Default.Assessment, "Reportes", "reports", onGoToReports))
        itemList.add(DrawerItem(Icons.Default.Group, "Usuarios y roles", "users", onRegister))
        itemList.add(DrawerItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión", "logout", onLogout))
    } else {
        itemList.add(DrawerItem(Icons.Default.Home, "Inicio", "home", onHome))
        itemList.add(DrawerItem(Icons.AutoMirrored.Filled.Login, "Iniciar Sesión", "login", onLogin))
=======
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
>>>>>>> Stashed changes
    }
    return itemList
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
