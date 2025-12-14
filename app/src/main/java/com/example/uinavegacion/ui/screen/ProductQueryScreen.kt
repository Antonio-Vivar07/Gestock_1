package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.ProductViewModel

@Composable
fun ProductQueryScreen(
    productVm: ProductViewModel,
    onProductClick: (Int) -> Unit,
    onScanClick: () -> Unit
) {
    val allProducts: List<ProductEntity> by productVm.allProducts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // ✅ Tipado explícito para evitar problemas de inferencia y ambigüedades
    val filteredProducts: List<ProductEntity> =
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allProducts.filter { p ->
                p.name.contains(searchQuery, ignoreCase = true) ||
                        p.code.contains(searchQuery, ignoreCase = true)
            }
        }

    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(onClick = onScanClick) {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "Escanear Código QR",
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por nombre o código...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredProducts) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text("Código: ${product.code}") },
                        trailingContent = { Text("Stock: ${product.stock}") },
                        modifier = Modifier.clickable { onProductClick(product.id) }
                    )
                    Divider()
                }
            }
        }
    }
}
