package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel
import com.example.uinavegacion.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSearchScreen(
    authVm: AuthViewModel,
    productVm: ProductViewModel,
    onProductClick: (Int) -> Unit
) {
    val session by authVm.session.collectAsState()
    val role = session?.role
    val isAdmin = role == UserRole.ADMINISTRADOR

    // Siempre refrescamos desde backend para evitar datos "fantasma" (si borraste algo en MongoDB)
    LaunchedEffect(Unit) { productVm.refreshProducts() }

    val allProducts by productVm.allProducts.collectAsState()
    val deletedProducts by productVm.deletedProducts.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Activos, 1 = Eliminados
    LaunchedEffect(activeTab, isAdmin) {
        if (isAdmin && activeTab == 1) {
            productVm.loadDeletedProducts()
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var dialogAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var dialogText by remember { mutableStateOf("") }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val defaultCategoryLabel = "Categoría"
    val availableCategories = listOf(defaultCategoryLabel) + allProducts
        .map { it.category.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
        .sorted()

    var selectedCategory by remember { mutableStateOf(defaultCategoryLabel) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val listToShow = if (!isAdmin || activeTab == 0) allProducts else deletedProducts

    val filteredProducts = listToShow.filter { product ->
        val categoryMatch = selectedCategory == defaultCategoryLabel || product.category.equals(selectedCategory, ignoreCase = true)
        val searchMatch = product.name.contains(searchQuery, ignoreCase = true) || product.code.contains(searchQuery, ignoreCase = true)
        categoryMatch && searchMatch
    }

    if (showConfirmDialog && dialogAction != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar") },
            text = { Text(dialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        dialogAction?.invoke()
                        showConfirmDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {

            // Tabs solo para ADMIN (Activos / Eliminados)
            if (isAdmin) {
                TabRow(selectedTabIndex = activeTab) {
                    Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Activos") })
                    Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Eliminados") })
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = it },
                    modifier = Modifier.weight(0.9f)
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredProducts) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = {
                            Text("Código: ${product.code} | Categoría: ${product.category} | Ubicación: ${product.zone}")
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Stock: ${product.stock}")
                                Spacer(Modifier.width(8.dp))

                                // Acciones:
                                // - TRABAJADOR: solo ver (si intenta borrar, mostramos mensaje)
                                // - ADMIN:
                                //   - Activos: borrar (soft delete)
                                //   - Eliminados: restaurar
                                if (isAdmin) {
                                    if (activeTab == 0) {
                                        IconButton(
                                            onClick = {
                                                dialogText = "¿Eliminar '${product.name}'? (se podrá restaurar)"
                                                dialogAction = {
                                                    productVm.deleteRemote(product) { msg -> snackbarMessage = msg }
                                                    productVm.refreshProducts()
                                                }
                                                showConfirmDialog = true
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                dialogText = "¿Restaurar '${product.name}' a su último estado?"
                                                dialogAction = {
                                                    productVm.restoreRemote(product) { msg -> snackbarMessage = msg }
                                                    productVm.loadDeletedProducts()
                                                }
                                                showConfirmDialog = true
                                            }
                                        ) {
                                            Icon(Icons.Default.Restore, contentDescription = "Restaurar", tint = Color.Gray)
                                        }
                                    }
                                } else {
                                    // TRABAJADOR: mostramos el ícono, pero bloqueamos la acción
                                    IconButton(onClick = { snackbarMessage = "No tienes permisos para realizar esta acción" }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Sin permisos", tint = Color.LightGray)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.clickable { onProductClick(product.id) }
                    )
                    Divider()
                }
            }
        }
    }
}
