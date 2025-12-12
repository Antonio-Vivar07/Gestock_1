package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSearchScreen(
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
        .map { it.category.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
        .sorted()
    var selectedCategory by remember { mutableStateOf(defaultCategoryLabel) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val filteredActiveProducts = activeProducts.filter { product ->
        val categoryMatch = selectedCategory == defaultCategoryLabel || product.category.equals(selectedCategory, ignoreCase = true)
        val searchMatch = product.name.contains(searchQuery, ignoreCase = true) || product.code.contains(searchQuery, ignoreCase = true)
        categoryMatch && searchMatch
    }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(expanded = isCategoryDropdownExpanded, onExpandedChange = { isCategoryDropdownExpanded = it }, modifier = Modifier.weight(0.8f)) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isCategoryDropdownExpanded, onDismissRequest = { isCategoryDropdownExpanded = false }) {
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
}
