package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
<<<<<<< Updated upstream
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
=======
import androidx.compose.material.icons.filled.Restore
>>>>>>> Stashed changes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel
<<<<<<< Updated upstream
import kotlinx.coroutines.launch
=======
import com.example.uinavegacion.viewmodel.UserRole
>>>>>>> Stashed changes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSearchScreen(
<<<<<<< Updated upstream
    productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onProductClick: (Int) -> Unit
) {
    val activeProducts by productVm.products.collectAsState()
    val uiState by productVm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    LaunchedEffect(activeProducts.size) {
        productVm.loadInactiveProducts()
    }

    val defaultCategoryLabel = "Categoría"
    val availableCategories = listOf(defaultCategoryLabel) + activeProducts
=======
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
>>>>>>> Stashed changes
        .map { it.category.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
        .sorted()
    var selectedCategory by remember { mutableStateOf(defaultCategoryLabel) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

<<<<<<< Updated upstream
    val filteredActiveProducts = activeProducts.filter { product ->
=======
    val listToShow = if (!isAdmin || activeTab == 0) allProducts else deletedProducts

    val filteredProducts = listToShow.filter { product ->
>>>>>>> Stashed changes
        val categoryMatch = selectedCategory == defaultCategoryLabel || product.category.equals(selectedCategory, ignoreCase = true)
        val searchMatch = product.name.contains(searchQuery, ignoreCase = true) || product.code.contains(searchQuery, ignoreCase = true)
        categoryMatch && searchMatch
    }

    if (showConfirmDialog && dialogAction != null) {
        AlertDialog(
<<<<<<< Updated upstream
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Borrado") },
            text = { Text("El producto '${productToDelete!!.name}' será archivado. Podrás restaurarlo más tarde.") },
            confirmButton = {
                Button(
                    onClick = {
                        productVm.deleteProduct(productToDelete!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Archivar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
=======
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
>>>>>>> Stashed changes
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier.weight(1f)
                )
<<<<<<< Updated upstream
                ExposedDropdownMenuBox(expanded = isCategoryDropdownExpanded, onExpandedChange = { isCategoryDropdownExpanded = it }, modifier = Modifier.weight(0.8f)) {
=======

                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = it },
                    modifier = Modifier.weight(0.9f)
                ) {
>>>>>>> Stashed changes
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
<<<<<<< Updated upstream
                    ExposedDropdownMenu(expanded = isCategoryDropdownExpanded, onDismissRequest = { isCategoryDropdownExpanded = false }) {
=======
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
>>>>>>> Stashed changes
                        availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
<<<<<<< Updated upstream
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        try {
                            productVm.syncProducts()
                            snackbarHostState.showSnackbar("Sincronización con el servidor completada.")
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error durante la sincronización: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Sync, contentDescription = "Sincronizar")
                Spacer(Modifier.width(8.dp))
                Text("Sincronizar con el Servidor")
            }
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredActiveProducts, key = { "active-${it.id}" }) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text("Código: ${product.code} | Categoría: ${product.category}") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Stock: ${product.stock}/${product.minStock}")
                                IconButton(onClick = { productToDelete = product; showDeleteDialog = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Archivar", tint = Color.Gray)
                                }
                            }
                        },
                        modifier = Modifier.clickable { onProductClick(product.id) }
                    )
                    Divider()
                }

                item(key = "archived-header") {
                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Productos Borrados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { productVm.loadInactiveProducts() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refrescar lista de borrados")
                        }
                    }
                    Divider(Modifier.padding(top = 8.dp))
                }

                if (uiState.isInactiveListLoading) {
                    item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                } else if (uiState.inactiveListError != null) {
                    item { Text(uiState.inactiveListError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }
                } else if (uiState.inactiveProducts.isEmpty()) {
                    item { Text("No hay productos borrados.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp)) }
                } else {
                    items(uiState.inactiveProducts, key = { "inactive-${it.remoteId}" }) { inactiveProduct ->
                        ArchivedProductListItem(
                            product = inactiveProduct,
                            onRestore = { productVm.restoreProduct(inactiveProduct) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivedProductListItem(
    product: ProductEntity,
    onRestore: () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(product.name, color = Color.Gray) },
        supportingContent = { Text("Código: ${product.code}", color = Color.Gray) },
        trailingContent = {
            IconButton(onClick = onRestore) {
                Icon(Icons.Default.RestoreFromTrash, contentDescription = "Restaurar", tint = MaterialTheme.colorScheme.primary)
            }
        }
    )
    Divider()
=======
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
>>>>>>> Stashed changes
}
