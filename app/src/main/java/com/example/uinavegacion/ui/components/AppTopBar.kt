package com.example.uinavegacion.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uinavegacion.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    onOpenDrawer: () -> Unit
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // --- PANTALLAS PRINCIPALES (MUESTRAN MENÚ) ---
    val topLevelRoutes = setOf(
        Route.Home.path, 
        Route.InventoryControl.path
    )

    // --- LÓGICA CORREGIDA ---
    // Comprueba si la ruta actual COMIENZA con alguna de las rutas principales.
    // Esto soluciona el problema de los parámetros (ej: "?message=...").
    val isTopLevel = topLevelRoutes.any { currentRoute?.startsWith(it) == true }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                text = "Demo Navegación Compose",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (isTopLevel) {
                // Si es una pantalla principal, muestra el icono del menú lateral
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menú"
                    )
                }
            } else {
                // Si NO es una pantalla principal, muestra la flecha para volver
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás"
                    )
                }
            }
        }
    )
}
