package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSearchScreen( 
    productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onProductClick: (Int) -> Unit
) {
    val allProducts by productVm.allProducts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    // --- LÓGICA DE FILTRO DE CATEGORÍAS CORREGIDA ---
    val defaultCategoryLabel = "Categoría"
    
    // 1. Normaliza y obtiene categorías únicas para el menú desplegable
    val availableCategories = listOf(defaultCategoryLabel) + allProducts
        .map { it.category.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() } // Clave de la corrección: distingue por minúsculas
        .sorted()

    var selectedCategory by remember { mutableStateOf(defaultCategoryLabel) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // 2. Filtra la lista de productos usando una comparación que ignora mayúsculas/minúsculas
    val filteredProducts = allProducts.filter { product ->
        val categoryMatch = selectedCategory == defaultCategoryLabel || product.category.equals(selectedCategory, ignoreCase = true)
        val searchMatch = product.name.contains(searchQuery, ignoreCase = true) || product.code.contains(searchQuery, ignoreCase = true)
        categoryMatch && searchMatch
    }

    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar el producto '${productToDelete!!.name}'?") },
            confirmButton = { Button(onClick = { productVm.deleteProduct(productToDelete!!); showDeleteDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredProducts) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("Código: ${product.code} | Categoría: ${product.category}") },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (product.stock > 0 && product.stock <= product.minStock) {
                                Badge(containerColor = Color(0xFFFFA726)) { Text("Bajo") }
                                Spacer(Modifier.width(8.dp))
                            } else if (product.stock == 0) {
                                Badge(containerColor = Color.Red) { Text("Agotado") }
                                Spacer(Modifier.width(8.dp))
                            }
                            Text("Stock: ${product.stock}")
                            IconButton(onClick = { productToDelete = product; showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
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